package com.jss.osiris.modules.invoicing.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.libs.transfer.AmtBean;
import com.jss.osiris.libs.transfer.CdtTrfTxInfBean;
import com.jss.osiris.libs.transfer.CdtrAcctBean;
import com.jss.osiris.libs.transfer.CdtrAgtBean;
import com.jss.osiris.libs.transfer.CdtrBean;
import com.jss.osiris.libs.transfer.CstmrCdtTrfInitnBean;
import com.jss.osiris.libs.transfer.CtgyPurpBean;
import com.jss.osiris.libs.transfer.DbtrAcctBean;
import com.jss.osiris.libs.transfer.DbtrAgtBean;
import com.jss.osiris.libs.transfer.DbtrBean;
import com.jss.osiris.libs.transfer.DocumentBean;
import com.jss.osiris.libs.transfer.FinInstnIdBean;
import com.jss.osiris.libs.transfer.GrpHdrBean;
import com.jss.osiris.libs.transfer.IdBean;
import com.jss.osiris.libs.transfer.InitgPtyBean;
import com.jss.osiris.libs.transfer.InstdAmtBean;
import com.jss.osiris.libs.transfer.OrgIdBean;
import com.jss.osiris.libs.transfer.OthrBean;
import com.jss.osiris.libs.transfer.OthrIdBean;
import com.jss.osiris.libs.transfer.PmtIdBean;
import com.jss.osiris.libs.transfer.PmtInfBean;
import com.jss.osiris.libs.transfer.PmtTpInfBean;
import com.jss.osiris.libs.transfer.PstlAdrBean;
import com.jss.osiris.libs.transfer.RmtInfBean;
import com.jss.osiris.libs.transfer.SvcLvlBean;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Deposit;
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
        return refundRepository.findRefunds(
                refundSearch.getStartDate().withHour(0).withMinute(0),
                refundSearch.getEndDate().withHour(23).withMinute(59), refundSearch.getMinAmount(),
                refundSearch.getMaxAmount(),
                refundSearch.getLabel(), refundSearch.isHideExportedRefunds(), refundSearch.isHideMatchedRefunds());
    }

    @Override
    public void generateRefund(ITiers tiersRefund, Affaire affaireRefund, Payment payment, Deposit deposit,
            Float amount)
            throws OsirisException, OsirisClientMessageException {
        Refund refund = new Refund();
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

        if (refund.getRefundIBAN() == null || refund.getRefundType() == null)
            throw new OsirisClientMessageException(
                    "IBAN non trouvé pour effectuer le remboursement. Merci de renseigner le tiers ou l'affaire. L'opération est annulée.");

        if (payment != null) {
            refund.setLabel("Remboursement du paiement n°" + payment.getId());
            refund.setPayment(payment);
        } else if (deposit != null) {
            refund.setLabel("Remboursement de l'acompte n°" + deposit
                    .getId());
            refund.setDeposit(deposit);
        }
        refund.setRefundAmount(amount);
        refund.setIsMatched(false);
        refund.setIsAlreadyExported(false);
        refund.setRefundDateTime(LocalDateTime.now());
        this.addOrUpdateRefund(refund);
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
            totalAmount += refund.getRefundAmount();

        XmlMapper xmlMapper = new XmlMapper();
        DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            DocumentBean document = new DocumentBean();
            document.setCstmrCdtTrfInitnBean(new CstmrCdtTrfInitnBean());

            GrpHdrBean header = new GrpHdrBean();
            document.getCstmrCdtTrfInitnBean().setGrpHdrBean(header);

            header.setMsgId("Virement JSS du " + LocalDateTime.now().format(formatterDateTime));
            header.setCreDtTm(LocalDateTime.now().format(formatterDateTime));
            header.setNbOfTxs(refunds.size());
            header.setCtrlSum(totalAmount);

            InitgPtyBean emiterDetails = new InitgPtyBean();
            header.setInitgPtyBean(emiterDetails);
            emiterDetails.setNm("SPPS - JSS COMPTE 00011");
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

            PmtInfBean body = new PmtInfBean();
            document.getCstmrCdtTrfInitnBean().setPmtInfBean(body);
            body.setPmtInfId(header.getMsgId());
            body.setPmtMtd("TRF");
            body.setBtchBookg(true);
            body.setNbOfTxs(refunds.size());
            body.setCtrlSum(totalAmount);

            PmtTpInfBean bodyTransfertType = new PmtTpInfBean();
            body.setPmtTpInfBean(bodyTransfertType);
            bodyTransfertType.setInstrPrty("NORM");

            SvcLvlBean transfertNorm = new SvcLvlBean();
            bodyTransfertType.setSvcLvlBean(transfertNorm);
            transfertNorm.setCd("SEPA");

            CtgyPurpBean transfertPurpose = new CtgyPurpBean();
            bodyTransfertType.setCtgyPurpBean(transfertPurpose);
            transfertPurpose.setCd("FOUR");

            body.setReqdExctnDt(LocalDateTime.now().plusDays(1).format(formatterDate));

            DbtrBean debiteur = new DbtrBean();
            body.setDbtrBean(debiteur);
            debiteur.setNm("SPPS - JSS COMPTE 00011");

            DbtrAcctBean account = new DbtrAcctBean();
            body.setDbtrAcctBean(account);
            IdBean accountId = new IdBean();
            accountId.setIban(ibanJss.replaceAll(" ", ""));
            account.setIdBean(accountId);

            DbtrAgtBean bic = new DbtrAgtBean();
            body.setDbtrAgtBean(bic);
            FinInstnIdBean financialInstitution = new FinInstnIdBean();
            financialInstitution.setBic(bicJss);
            bic.setFinInstnIdBean(financialInstitution);

            body.setCdtTrfTxInfBeanList(new ArrayList<CdtTrfTxInfBean>());

            for (RefundSearchResult refund : refunds) {
                Refund completeRefund = getRefund(refund.getId());

                CdtTrfTxInfBean virement = new CdtTrfTxInfBean();
                PmtIdBean virementId = new PmtIdBean();
                virement.setPmtIdBean(virementId);
                virementId.setEndToEndId("2-Virement JSS du " + LocalDateTime.now().format(formatterDateTime));
                virementId.setInstrId("2-Virement JSS du " + LocalDateTime.now().format(formatterDateTime));

                AmtBean currency = new AmtBean();
                virement.setAmtBean(currency);
                InstdAmtBean currencyDetails = new InstdAmtBean();
                currencyDetails.setCcy("EUR");
                currencyDetails.setValue(refund.getRefundAmount() + "");
                currency.setInstdAmtBean(currencyDetails);

                CdtrAgtBean bicVirement = new CdtrAgtBean();
                virement.setCdtrAgtBeanList(Arrays.asList(bicVirement));
                FinInstnIdBean bicVirementId = new FinInstnIdBean();
                bicVirement.setFinInstnIdBean(financialInstitution);
                bicVirementId.setBic(bicJss);

                String customerLabel = "";
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

                CdtrBean customerOrder = new CdtrBean();
                virement.setCdtrBeanList(Arrays.asList(customerOrder));
                customerOrder.setNm(customerLabel);

                CdtrAcctBean customerAccount = new CdtrAcctBean();
                virement.setCdtrAcctBean(customerAccount);
                IdBean customerAccountId = new IdBean();
                customerAccount.setIdBean(customerAccountId);
                customerAccountId.setIban(completeRefund.getRefundIBAN());

                RmtInfBean virementLabel = new RmtInfBean();
                virement.setRmtInfBean(virementLabel);
                virementLabel.setUstrd("JSS - " + refund.getRefundLabel());

                body.getCdtTrfTxInfBeanList().add(virement);

            }

            xml = xmlMapper.writeValueAsString(document);

            xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml;
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
