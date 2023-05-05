package com.jss.osiris.modules.invoicing.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.libs.transfer.CstmrCdtTrfInitnBean;
import com.jss.osiris.libs.transfer.DocumentBean;
import com.jss.osiris.libs.transfer.GrpHdrBean;
import com.jss.osiris.libs.transfer.InitgPtyBean;
import com.jss.osiris.libs.transfer.OrgIdBean;
import com.jss.osiris.libs.transfer.OthrBean;
import com.jss.osiris.libs.transfer.OthrIdBean;
import com.jss.osiris.libs.transfer.PmtInfBean;
import com.jss.osiris.libs.transfer.PstlAdrBean;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Appoint;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.invoicing.model.RefundSearch;
import com.jss.osiris.modules.invoicing.model.RefundSearchResult;
import com.jss.osiris.modules.invoicing.repository.RefundRepository;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.service.BankTransfertService;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Tiers;

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

    @Autowired
    IndexEntityService indexEntityService;

    @Value("${jss.iban}")
    private String ibanJss;

    @Value("${jss.bic}")
    private String bicJss;

    @Autowired
    BankTransfertService bankTransfertService;

    @Autowired
    AppointService appointService;

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
            Refund refund) {
        refundRepository.save(refund);
        indexEntityService.indexEntity(refund, refund.getId());
        return refund;
    }

    @Override
    public void reindexRefunds() {
        List<Refund> refunds = getRefunds();
        if (refunds != null)
            for (Refund refund : refunds)
                indexEntityService.indexEntity(refund, refund.getId());
    }

    @Override
    public List<RefundSearchResult> searchRefunds(RefundSearch refundSearch) {
        if (refundSearch.getStartDate() == null)
            refundSearch.setStartDate(LocalDateTime.now().minusYears(100));
        if (refundSearch.getEndDate() == null)
            refundSearch.setEndDate(LocalDateTime.now().plusYears(100));
        return refundRepository.findRefunds(
                refundSearch.getStartDate().withHour(0).withMinute(0),
                refundSearch.getEndDate().withHour(23).withMinute(59), refundSearch.getMinAmount(),
                refundSearch.getMaxAmount(),
                refundSearch.getLabel(), refundSearch.isHideExportedRefunds(), refundSearch.isHideMatchedRefunds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean generateRefundForAppoint(Integer idAppoint) throws OsirisException, OsirisClientMessageException {
        Appoint appoint = appointService.getAppoint(idAppoint);
        if (appoint.getInvoice() != null) {
            Invoice invoice = appoint.getInvoice();
            ITiers tiers = null;
            if (invoice.getResponsable() != null)
                tiers = invoice.getResponsable().getTiers();
            if (invoice.getTiers() != null)
                tiers = invoice.getTiers();
            if (invoice.getConfrere() != null)
                tiers = invoice.getConfrere();

            Affaire affaire = null;
            if (invoice.getBillingLabelType().getId().equals(constantService.getBillingLabelTypeCodeAffaire().getId()))
                affaire = invoice.getCustomerOrder().getAssoAffaireOrders().get(0).getAffaire();

            generateRefund(tiers, affaire, null, null, appoint.getAppointAmount(), " appoint n°" + appoint.getId(),
                    null, appoint);
        }
        return true;
    }

    @Override
    public Refund generateRefund(ITiers tiersRefund, Affaire affaireRefund, Payment payment, Deposit deposit,
            Float amount, String labelSuffix, CustomerOrder customerOrder, Appoint appoint)
            throws OsirisException, OsirisClientMessageException {
        Refund refund = new Refund();
        refund.setCustomerOrder(customerOrder);
        if (tiersRefund instanceof Confrere)
            refund.setConfrere((Confrere) tiersRefund);
        if (tiersRefund instanceof Tiers)
            refund.setTiers((Tiers) tiersRefund);
        if (affaireRefund != null) {
            refund.setAffaire(affaireRefund);
            refund.setRefundIBAN(affaireRefund.getPaymentIban());
            refund.setRefundBic(affaireRefund.getPaymentBic());
            refund.setRefundType(constantService.getRefundTypeVirement());
        } else {
            Document refundDocument = documentService.getRefundDocument(tiersRefund.getDocuments());
            if (refundDocument != null) {
                refund.setRefundType(refundDocument.getRefundType());
                if (refundDocument.getRegie() != null && refundDocument.getRegie().getIban() != null
                        && !refundDocument.getRegie().getIban().equals("")) {
                    refund.setRefundIBAN(refundDocument.getRegie().getIban());
                    refund.setRefundBic(refundDocument.getRegie().getBic());
                } else {
                    refund.setRefundIBAN(refundDocument.getRefundIBAN());
                    refund.setRefundBic(refundDocument.getRefundBic());
                }
            }
        }

        if (refund.getRefundIBAN() == null || refund.getRefundBic() == null)
            throw new OsirisClientMessageException(
                    "IBAN non trouvé pour effectuer le remboursement. Merci de renseigner le tiers ou l'affaire. L'opération est annulée.");

        refund.setRefundIBAN(refund.getRefundIBAN().replaceAll(" ", ""));
        refund.setRefundBic(refund.getRefundBic().replaceAll(" ", ""));

        if (payment != null) {
            refund.setLabel("Remboursement du paiement N " + payment.getId());
            refund.setPayment(payment);
        } else if (deposit != null) {
            refund.setLabel("Remboursement de l'acompte N " + deposit
                    .getId());
            refund.setDeposit(deposit);
        } else if (appoint != null) {
            refund.setLabel("Remboursement de l'appoint N " + appoint
                    .getId());
            refund.setAppoint(appoint);
        }
        if (labelSuffix != null)
            refund.setLabel(refund.getLabel() + " / " + labelSuffix);

        if (refund.getCustomerOrder() != null && refund.getCustomerOrder().getAssoAffaireOrders() != null) {
            Affaire affaire = refund.getCustomerOrder().getAssoAffaireOrders().get(0).getAffaire();
            refund.setLabel(refund.getLabel() + " / " + (affaire.getDenomination() != null ? affaire.getDenomination()
                    : (affaire.getFirstname() + " " + affaire.getLastname())));
        }

        refund.setRefundAmount(amount);
        refund.setIsMatched(false);
        refund.setIsAlreadyExported(false);
        refund.setRefundDateTime(LocalDateTime.now());
        return this.addOrUpdateRefund(refund);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public File getRefundExport(RefundSearch refundSearch) throws OsirisException {

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
                                        139)));

                completeRefund.setIsAlreadyExported(true);
                addOrUpdateRefund(completeRefund);

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
