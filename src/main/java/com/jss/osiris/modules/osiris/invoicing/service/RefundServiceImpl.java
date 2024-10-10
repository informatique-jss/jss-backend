package com.jss.osiris.modules.osiris.invoicing.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.transfer.CstmrCdtTrfInitnBean;
import com.jss.osiris.libs.transfer.DocumentBean;
import com.jss.osiris.libs.transfer.GrpHdrBean;
import com.jss.osiris.libs.transfer.InitgPtyBean;
import com.jss.osiris.libs.transfer.OrgIdBean;
import com.jss.osiris.libs.transfer.OthrBean;
import com.jss.osiris.libs.transfer.OthrIdBean;
import com.jss.osiris.libs.transfer.PmtInfBean;
import com.jss.osiris.libs.transfer.PstlAdrBean;
import com.jss.osiris.modules.osiris.accounting.service.AccountingRecordGenerationService;
import com.jss.osiris.modules.osiris.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.model.Refund;
import com.jss.osiris.modules.osiris.invoicing.model.RefundSearch;
import com.jss.osiris.modules.osiris.invoicing.model.RefundSearchResult;
import com.jss.osiris.modules.osiris.invoicing.repository.RefundRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.service.BankTransfertService;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    RefundRepository refundRepository;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    ConstantService constantService;

    @Autowired
    DocumentService documentService;

    @Value("${jss.iban}")
    private String ibanJss;

    @Value("${jss.bic}")
    private String bicJss;

    @Autowired
    BankTransfertService bankTransfertService;

    @Autowired
    AccountingRecordGenerationService accountingRecordGenerationService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    BatchService batchService;

    @Override
    public List<Refund> getRefunds() {
        return IterableUtils.toList(refundRepository.findAll());
    }

    @Override
    public Refund getRefund(Integer id) {
        Optional<Refund> refund = refundRepository.findById(id);
        if (refund.isPresent())
            return refund.get();
        return null;
    }

    @Override
    public Refund addOrUpdateRefund(
            Refund refund) throws OsirisException {
        refundRepository.save(refund);
        batchService.declareNewBatch(Batch.REINDEX_REFUND, refund.getId());
        return refund;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reindexRefunds() throws OsirisException {
        List<Refund> refunds = getRefunds();
        if (refunds != null)
            for (Refund refund : refunds)
                batchService.declareNewBatch(Batch.REINDEX_REFUND, refund.getId());
    }

    @Override
    public List<RefundSearchResult> searchRefunds(RefundSearch refundSearch) {
        if (refundSearch.getStartDate() == null)
            refundSearch.setStartDate(LocalDateTime.now().minusYears(100));
        if (refundSearch.getEndDate() == null)
            refundSearch.setEndDate(LocalDateTime.now().plusYears(100));
        if (refundSearch.getIdRefund() == null)
            refundSearch.setIdRefund(0);
        return refundRepository.findRefunds(
                refundSearch.getStartDate().withHour(0).withMinute(0),
                refundSearch.getEndDate().withHour(23).withMinute(59), refundSearch.getMinAmount(),
                refundSearch.getMaxAmount(),
                refundSearch.getLabel(), refundSearch.isHideExportedRefunds(), refundSearch.isHideMatchedRefunds(),
                refundSearch.getIdRefund());
    }

    @Override
    public Refund refundPayment(Tiers tiersRefund, Affaire affaireRefund, Tiers tiersOrder, Payment payment,
            BigDecimal amount, CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        if (payment == null)
            throw new OsirisClientMessageException("Paiment annulé");

        if (tiersOrder == null)
            throw new OsirisClientMessageException("Tiers non trouvé");

        Refund refund = new Refund();
        Document refundDocument = null;
        refund.setCustomerOrder(customerOrder);
        refund.setRefundType(constantService.getRefundTypeVirement());

        if (tiersRefund != null) {
            refund.setTiers(tiersRefund);
            refundDocument = documentService.getRefundDocument(tiersRefund.getDocuments());
        }
        if (affaireRefund != null) {
            refund.setAffaire(affaireRefund);
            refund.setRefundIBAN(affaireRefund.getPaymentIban());
            refund.setRefundBic(affaireRefund.getPaymentBic());
            refund.setTiers(tiersOrder);
        } else if (refundDocument != null) {
            refund.setRefundType(refundDocument.getRefundType());
            refund.setRefundIBAN(refundDocument.getRefundIBAN());
            refund.setRefundBic(refundDocument.getRefundBic());
        }

        if (refund.getRefundIBAN() == null || refund.getRefundBic() == null)
            throw new OsirisClientMessageException(
                    "IBAN non trouvé pour effectuer le remboursement. Merci de renseigner le tiers ou l'affaire. L'opération est annulée.");

        refund.setRefundIBAN(refund.getRefundIBAN().replaceAll(" ", ""));
        refund.setRefundBic(refund.getRefundBic().replaceAll(" ", ""));

        String paymentType = "";
        if (customerOrder != null)
            paymentType = "de la commande N " + customerOrder.getId();
        else {
            if (payment.getIsAppoint() != null && payment.getIsAppoint())
                paymentType = "de l'appoint N " + payment.getId();
            else
                paymentType = "du paiement N " + payment.getId();
        }
        refund.setLabel("Remboursement " + paymentType);

        if (refund.getCustomerOrder() != null && refund.getCustomerOrder().getAssoAffaireOrders() != null) {
            Affaire affaire = refund.getCustomerOrder().getAssoAffaireOrders().get(0).getAffaire();
            refund.setLabel((affaire.getDenomination() != null ? affaire.getDenomination()
                    : (affaire.getFirstname() + " " + affaire.getLastname())) + " / " + refund.getLabel());
        }

        refund.setRefundAmount(amount);
        refund.setIsMatched(false);
        refund.setIsAlreadyExported(false);
        refund.setRefundDateTime(LocalDateTime.now());
        this.addOrUpdateRefund(refund);

        refund.setPayments(new ArrayList<Payment>());
        refund.getPayments()
                .add(paymentService.generateNewRefundPayment(refund, amount.negate(), refund.getTiers(), payment));
        if (customerOrder == null) // If it's a payment / appoint refund
            paymentService.cancelAppoint(payment);

        if (payment.getIsAppoint() == false)
            accountingRecordGenerationService.generateAccountingRecordsForRefundGeneration(refund);

        return this.addOrUpdateRefund(refund);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public File getRefundExport(RefundSearch refundSearch)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {

        List<RefundSearchResult> refunds = searchRefunds(refundSearch);
        String xml = "";

        if (refunds == null || refunds.size() == 0)
            return null;

        Float totalAmount = 0f;
        for (RefundSearchResult refund : refunds)
            totalAmount += Math.round(refund.getRefundAmount() * 100f) / 100f;

        XmlMapper xmlMapper = new XmlMapper();
        DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            DocumentBean document = new DocumentBean();
            document.setCstmrCdtTrfInitnBean(new CstmrCdtTrfInitnBean());

            GrpHdrBean header = new GrpHdrBean();
            document.getCstmrCdtTrfInitnBean().setGrpHdrBean(header);

            header.setMsgId("Virement JSS du " + LocalDateTime.now().format(formatterDate));
            header.setCreDtTm(LocalDateTime.now().format(formatterDateTime));
            header.setNbOfTxs(refunds.size());
            header.setCtrlSum(Math.round(totalAmount * 100f) / 100f);

            InitgPtyBean emiterDetails = new InitgPtyBean();
            header.setInitgPtyBean(emiterDetails);
            emiterDetails.setNm("SPPS - JSS REMBOURSEMENT");
            PstlAdrBean address = new PstlAdrBean();
            address.setCtry("FR");
            emiterDetails.setPstlAdrBean(address);

            address.setAdrLine("8 RUE SAINT AUGUSTIN 75002 PARIS");

            OrgIdBean id = new OrgIdBean();
            emiterDetails.setIdBeanList(id);
            OthrIdBean orgId = new OthrIdBean();
            id.setOrgIdBean(orgId);
            OthrBean othrBean = new OthrBean();
            orgId.setOthrBean(othrBean);
            othrBean.setId("55207462700035");

            document.getCstmrCdtTrfInitnBean().setPmtInfBean(new ArrayList<PmtInfBean>());

            for (RefundSearchResult refund : refunds) {

                String customerLabel = "";
                Refund completeRefund = getRefund(refund.getId());
                if (completeRefund.getAffaire() != null)
                    customerLabel = completeRefund.getAffaire().getDenomination() != null
                            ? completeRefund.getAffaire().getDenomination()
                            : (completeRefund.getAffaire().getFirstname() + " "
                                    + completeRefund.getAffaire().getFirstname());
                else if (completeRefund.getConfrere() != null)
                    customerLabel = completeRefund.getLabel();
                else if (completeRefund.getTiers() != null)
                    customerLabel = completeRefund.getTiers().getDenomination() != null
                            ? completeRefund.getTiers().getDenomination()
                            : (completeRefund.getTiers().getFirstname() + " "
                                    + completeRefund.getTiers().getLastname());

                document.getCstmrCdtTrfInitnBean().getPmtInfBean()
                        .add(bankTransfertService.generateBodyForBankTransfert(header.getMsgId(),
                                refund.getRefundAmount(), refund.getRefundDate().toLocalDate(), customerLabel,
                                refund.getRefundIban().replaceAll(" ", ""),
                                completeRefund.getRefundBic().replaceAll(" ", ""),
                                StringUtils.substring((refund.getId() + " - " + refund.getRefundLabel()), 0,
                                        139),
                                false));

                if (!completeRefund.getIsAlreadyExported()) {
                    accountingRecordGenerationService.generateAccountingRecordsForRefundExport(completeRefund);
                    addOrUpdateRefund(completeRefund);
                }

                completeRefund.setIsAlreadyExported(true);
            }

            xml = xmlMapper.writeValueAsString(document);

            xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml;
            // Non supporting characters for bank ...
            xml = xml.replaceAll("°", " ");
        } catch (JsonProcessingException e2) {
            throw new OsirisException(null, "Impossible to generate XML file for refund export");
        }
        File xmlOutputFile;
        try {
            xmlOutputFile = File.createTempFile("refundExport", "xml");
        } catch (IOException e1) {
            throw new OsirisException(null, "Impossible to create temp file for refund export");
        }

        try (PrintWriter pw = new PrintWriter(xmlOutputFile)) {
            pw.write(xml);
            pw.close();
        } catch (FileNotFoundException e) {
            throw new OsirisException(e, "Impossible to read file " + xmlOutputFile.getAbsolutePath());
        }

        return xmlOutputFile;
    }
}
