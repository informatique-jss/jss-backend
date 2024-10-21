package com.jss.osiris.modules.osiris.quotation.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.transfer.CdtrSchmeIdBean;
import com.jss.osiris.libs.transfer.CdtrSchmeIdBeanIdBean;
import com.jss.osiris.libs.transfer.CstmrDrctDbtInitnBean;
import com.jss.osiris.libs.transfer.DbtrAcctBean;
import com.jss.osiris.libs.transfer.DbtrAgtBean;
import com.jss.osiris.libs.transfer.DbtrBean;
import com.jss.osiris.libs.transfer.DocumentBean;
import com.jss.osiris.libs.transfer.DrctDbtTxBean;
import com.jss.osiris.libs.transfer.DrctDbtTxInfBean;
import com.jss.osiris.libs.transfer.FinInstnIdBean;
import com.jss.osiris.libs.transfer.GrpHdrBean;
import com.jss.osiris.libs.transfer.IdBean;
import com.jss.osiris.libs.transfer.InitgPtyBean;
import com.jss.osiris.libs.transfer.InstdAmtBean;
import com.jss.osiris.libs.transfer.LclInstrmBean;
import com.jss.osiris.libs.transfer.MndtRltdInfBean;
import com.jss.osiris.libs.transfer.PmtIdBean;
import com.jss.osiris.libs.transfer.PmtInfBean;
import com.jss.osiris.libs.transfer.PmtTpInfBean;
import com.jss.osiris.libs.transfer.PrvtIdBean;
import com.jss.osiris.libs.transfer.PrvtOtherBean;
import com.jss.osiris.libs.transfer.RmtInfBean;
import com.jss.osiris.libs.transfer.SchmeNmBean;
import com.jss.osiris.libs.transfer.SvcLvlBean;
import com.jss.osiris.modules.osiris.accounting.service.AccountingRecordGenerationService;
import com.jss.osiris.modules.osiris.invoicing.model.DirectDebitTransfertSearch;
import com.jss.osiris.modules.osiris.invoicing.model.DirectDebitTransfertSearchResult;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.invoicing.service.PaymentService;
import com.jss.osiris.modules.osiris.quotation.model.DirectDebitTransfert;
import com.jss.osiris.modules.osiris.quotation.repository.DirectDebitTransfertRepository;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class DirectDebitTransfertServiceImpl implements DirectDebitTransfertService {

    @Autowired
    DirectDebitTransfertRepository directDebitTransfertRepository;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Autowired
    InvoiceService invoiceService;

    @Value("${jss.iban}")
    private String ibanJss;

    @Value("${jss.bic}")
    private String bicJss;

    @Value("${jss.sepa.identification}")
    private String jssSepaIdentification;

    @Autowired
    PaymentService paymentService;

    @Autowired
    AccountingRecordGenerationService accountingRecordGenerationService;

    @Autowired
    BatchService batchService;

    @Override
    public List<DirectDebitTransfert> getDirectDebitTransferts() {
        return IterableUtils.toList(directDebitTransfertRepository.findAll());
    }

    @Override
    public DirectDebitTransfert getDirectDebitTransfert(Integer id) {
        Optional<DirectDebitTransfert> directDebitTransfert = directDebitTransfertRepository.findById(id);
        if (directDebitTransfert.isPresent())
            return directDebitTransfert.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DirectDebitTransfert addOrUpdateDirectDebitTransfert(
            DirectDebitTransfert directDebitTransfert) throws OsirisException {
        if (directDebitTransfert.getIsMatched() == null)
            directDebitTransfert.setIsMatched(false);
        DirectDebitTransfert transfert = directDebitTransfertRepository.save(directDebitTransfert);
        batchService.declareNewBatch(Batch.REINDEX_DIRECT_DEBIT_BANK_TRANSFERT, transfert.getId());
        return transfert;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reindexDirectDebitTransfert() throws OsirisException {
        List<DirectDebitTransfert> directDebitTransferts = getDirectDebitTransferts();
        if (directDebitTransferts != null)
            for (DirectDebitTransfert directDebitTransfert : directDebitTransferts)
                batchService.declareNewBatch(Batch.REINDEX_DIRECT_DEBIT_BANK_TRANSFERT, directDebitTransfert.getId());
    }

    @Override
    public List<DirectDebitTransfertSearchResult> searchDirectDebitTransfert(
            DirectDebitTransfertSearch directDebitTransfertSearch) {
        if (directDebitTransfertSearch.getStartDate() == null)
            directDebitTransfertSearch.setStartDate(LocalDateTime.now().minusYears(100));
        if (directDebitTransfertSearch.getEndDate() == null)
            directDebitTransfertSearch.setEndDate(LocalDateTime.now().plusYears(100));
        if (directDebitTransfertSearch.getIdDirectDebitTransfert() == null)
            directDebitTransfertSearch.setIdDirectDebitTransfert(0);
        return directDebitTransfertRepository.findTransferts(
                directDebitTransfertSearch.getStartDate().withHour(0).withMinute(0),
                directDebitTransfertSearch.getEndDate().withHour(23).withMinute(59),
                directDebitTransfertSearch.getMinAmount(),
                directDebitTransfertSearch.getMaxAmount(),
                directDebitTransfertSearch.getLabel(), directDebitTransfertSearch.isHideMatchedDirectDebitTransfert(),
                directDebitTransfertSearch.isHideExportedDirectDebitTransfert(),
                directDebitTransfertSearch.getIdDirectDebitTransfert());

    }

    @Override
    public DirectDebitTransfert cancelDirectDebitTransfert(DirectDebitTransfert directDebitTransfert)
            throws OsirisException {
        directDebitTransfert.setIsCancelled(true);
        return addOrUpdateDirectDebitTransfert(directDebitTransfert);
    }

    @Override
    public DirectDebitTransfert generateDirectDebitTransfertForOutboundInvoice(Invoice invoice)
            throws OsirisException, OsirisClientMessageException {

        Responsable responsable = invoice.getResponsable();
        Integer sepaReference = null;
        LocalDate sepaDate = null;
        String customerOrderLabel = "";
        sepaReference = responsable.getTiers().getId();
        sepaDate = responsable.getTiers().getSepaMandateSignatureDate();
        customerOrderLabel = responsable.getTiers().getDenomination() != null
                ? responsable.getTiers().getDenomination()
                : responsable.getTiers().getFirstname() + " "
                        + responsable.getTiers().getLastname();

        if (sepaReference == null)
            throw new OsirisClientMessageException("Référence du mandat SEPA non trouvée sur le donneur d'ordre");

        if (sepaDate == null)
            throw new OsirisClientMessageException("Date du mandat SEPA non trouvée sur le donneur d'ordre");

        if (invoice.getResponsable().getTiers().getPaymentIban() == null
                || invoice.getResponsable().getTiers().getPaymentBic() == null)
            throw new OsirisClientMessageException("IBAN / BIC non trouvée sur le donneur d'ordre");

        DirectDebitTransfert directDebitTransfert = new DirectDebitTransfert();
        directDebitTransfert.setLabel("Facture " + invoice.getId() + " / Journal Spécial des Sociétés / "
                + (invoice.getCustomerOrder() != null ? invoice.getCustomerOrder().getId() : ""));
        directDebitTransfert.setIsAlreadyExported(false);
        BigDecimal totalPrice = invoiceService.getRemainingAmountToPayForInvoice(invoice);
        directDebitTransfert.setTransfertAmount(totalPrice);
        directDebitTransfert.setTransfertDateTime(invoice.getDueDate().atTime(12, 0));
        directDebitTransfert.setTransfertIban(invoice.getResponsable().getTiers().getPaymentIban());
        directDebitTransfert.setTransfertBic(invoice.getResponsable().getTiers().getPaymentBic());

        if (directDebitTransfert.getTransfertIban() == null || directDebitTransfert.getTransfertBic() == null)
            throw new OsirisException(null, "IBAN or BIC not found for direct debit transfert");

        directDebitTransfert.setTransfertIban(directDebitTransfert.getTransfertIban().replaceAll(" ", ""));
        directDebitTransfert.setTransfertBic(directDebitTransfert.getTransfertBic().replaceAll(" ", ""));

        directDebitTransfert.setSepaMandateReference(sepaReference + "");
        directDebitTransfert.setSepaMandateSignatureDate(sepaDate);
        directDebitTransfert.setIsCancelled(false);
        directDebitTransfert.setCustomerOrderLabel(customerOrderLabel);
        return this.addOrUpdateDirectDebitTransfert(directDebitTransfert);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public File getDirectDebitTransfertExport(DirectDebitTransfertSearch transfertSearch)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {

        List<DirectDebitTransfertSearchResult> bankTransferts = searchDirectDebitTransfert(transfertSearch);
        String xml = "";

        if (bankTransferts == null || bankTransferts.size() == 0)
            return null;

        Float totalAmount = 0f;
        for (DirectDebitTransfertSearchResult bankTransfert : bankTransferts)
            totalAmount += Math.round(bankTransfert.getTransfertAmount() * 100f) / 100f;

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            DocumentBean document = new DocumentBean();
            document.setCstmrDrctDbtInitn(new CstmrDrctDbtInitnBean());

            GrpHdrBean header = new GrpHdrBean();
            document.getCstmrDrctDbtInitn().setGrpHdrBean(header);

            header.setMsgId("Prélèvements JSS du " + LocalDateTime.now().format(formatterDate));
            header.setCreDtTm(LocalDateTime.now().format(formatterDateTime));
            header.setNbOfTxs(bankTransferts.size());
            header.setCtrlSum(Math.round(totalAmount * 100f) / 100f);

            InitgPtyBean emiterDetails = new InitgPtyBean();
            header.setInitgPtyBean(emiterDetails);
            emiterDetails.setNm("JOURNAL SPECIAL DES SOCIETES");

            document.getCstmrDrctDbtInitn().setPmtInfBean(new ArrayList<PmtInfBean>());

            for (DirectDebitTransfertSearchResult bankTransfert : bankTransferts) {

                PmtInfBean body = new PmtInfBean();
                document.getCstmrDrctDbtInitn().getPmtInfBean().add(body);
                body.setPmtInfId(bankTransfert.getId() + "");
                body.setPmtMtd("DD");
                body.setBtchBookg(false);
                body.setNbOfTxs(1);
                body.setCtrlSum(Math.round(bankTransfert.getTransfertAmount() * 100f) / 100f);

                PmtTpInfBean bodyTransfertType = new PmtTpInfBean();
                body.setPmtTpInfBean(bodyTransfertType);

                LclInstrmBean lclInstrmBean = new LclInstrmBean();
                lclInstrmBean.setCd("CORE");
                bodyTransfertType.setLclInstrmBean(lclInstrmBean);

                SvcLvlBean transfertNorm = new SvcLvlBean();
                bodyTransfertType.setSvcLvlBean(transfertNorm);

                bodyTransfertType.setSvcLvlBean(transfertNorm);
                transfertNorm.setCd("SEPA");

                bodyTransfertType.setSeqTp("RCUR");

                LocalDateTime transfertDate = bankTransfert.getTransfertDate();
                if (transfertDate.isBefore(LocalDateTime.now()))
                    transfertDate = LocalDateTime.now();

                body.setReqdColltnDt(transfertDate.format(formatterDate));

                DbtrBean debiteur = new DbtrBean();
                body.setCdtrBean(debiteur);
                debiteur.setNm("JOURNAL SPECIAL DES SOCIETES");

                DbtrAcctBean account = new DbtrAcctBean();
                body.setCdtrAcctBean(account);
                IdBean accountId = new IdBean();
                accountId.setIban(ibanJss.replaceAll(" ", ""));
                account.setIdBean(accountId);

                DbtrAgtBean bic = new DbtrAgtBean();
                body.setCdtrAgtBean(bic);
                FinInstnIdBean financialInstitution = new FinInstnIdBean();
                financialInstitution.setBic(bicJss);
                bic.setFinInstnIdBean(financialInstitution);

                CdtrSchmeIdBean cdtrSchmeIdBean = new CdtrSchmeIdBean();
                body.setCdtrSchmeIdBean(cdtrSchmeIdBean);

                CdtrSchmeIdBeanIdBean cdtrSchmeIdBeanIdBean = new CdtrSchmeIdBeanIdBean();
                cdtrSchmeIdBean.setIdBean(cdtrSchmeIdBeanIdBean);

                PrvtIdBean prvtIdBean = new PrvtIdBean();
                cdtrSchmeIdBeanIdBean.setPrvtIdBean(prvtIdBean);

                PrvtOtherBean prvtOtherBean = new PrvtOtherBean();
                prvtIdBean.setPrvtOtherBean(prvtOtherBean);

                SchmeNmBean schmeNmBean = new SchmeNmBean();
                prvtOtherBean.setSchmeNmBean(schmeNmBean);

                schmeNmBean.setPrtry("SEPA");
                prvtOtherBean.setId(jssSepaIdentification);

                body.setDrctDbtTxInfBeanList(new ArrayList<DrctDbtTxInfBean>());

                DirectDebitTransfert completeTransfert = getDirectDebitTransfert(bankTransfert.getId());

                DrctDbtTxInfBean prelevement = new DrctDbtTxInfBean();
                PmtIdBean virementId = new PmtIdBean();
                prelevement.setPmtIdBean(virementId);
                virementId.setEndToEndId("2-Prélèvement JSS du " + LocalDateTime.now().format(formatterDate));
                virementId.setInstrId("2-Prélèvement JSS du " + LocalDateTime.now().format(formatterDate));

                InstdAmtBean currencyDetails = new InstdAmtBean();
                currencyDetails.setCcy("EUR");
                currencyDetails.setValue(Math.round(bankTransfert.getTransfertAmount() * 100f) / 100f + "");
                prelevement.setInstdAmtBean(currencyDetails);

                DrctDbtTxBean debitIdentifier = new DrctDbtTxBean();
                MndtRltdInfBean debitIdentifierDetails = new MndtRltdInfBean();
                debitIdentifier.setMndtRltdInfBean(debitIdentifierDetails);
                prelevement.setDrctDbtTxBean(debitIdentifier);
                debitIdentifierDetails
                        .setDtOfSgntr(completeTransfert.getSepaMandateSignatureDate().format(formatterDate));
                debitIdentifierDetails
                        .setMndtId(completeTransfert.getSepaMandateReference());

                DbtrAgtBean bicVirement = new DbtrAgtBean();
                prelevement.setDbtrAgtBean(bicVirement);
                FinInstnIdBean bicVirementId = new FinInstnIdBean();
                bicVirement.setFinInstnIdBean(bicVirementId);
                bicVirementId.setBic(bankTransfert.getTransfertBic());

                DbtrBean customerOrder = new DbtrBean();
                prelevement.setDbtrBean(customerOrder);
                customerOrder.setNm(StringUtils.substring(completeTransfert.getCustomerOrderLabel(), 0, 139));

                DbtrAcctBean customerAccount = new DbtrAcctBean();
                prelevement.setDbtrAcctBean(customerAccount);
                IdBean customerAccountId = new IdBean();
                customerAccount.setIdBean(customerAccountId);
                customerAccountId.setIban(completeTransfert.getTransfertIban().replaceAll(" ", ""));

                RmtInfBean virementLabel = new RmtInfBean();
                prelevement.setRmtInfBean(virementLabel);
                virementLabel.setUstrd(
                        StringUtils.substring(completeTransfert.getId() + " " + completeTransfert.getLabel(), 0, 139));

                body.getDrctDbtTxInfBeanList().add(prelevement);

                if (completeTransfert.getIsAlreadyExported() == null
                        || completeTransfert.getIsAlreadyExported() == false) {
                    completeTransfert.setIsAlreadyExported(true);
                    addOrUpdateDirectDebitTransfert(completeTransfert);

                    Payment payment = paymentService.generateNewDirectDebitPayment(
                            completeTransfert.getTransfertAmount(), completeTransfert.getLabel(), completeTransfert);
                    accountingRecordGenerationService.generateAccountingRecordOnIncomingPaymentCreation(payment, false);

                    paymentService.manualMatchPaymentInvoicesAndCustomerOrders(payment,
                            Arrays.asList(completeTransfert.getInvoices().get(0)), null, null, null, null,
                            null);
                }
                addOrUpdateDirectDebitTransfert(completeTransfert);
            }

            document.setXmlns("urn:iso:std:iso:20022:tech:xsd:pain.008.001.02");
            xml = xmlMapper.writeValueAsString(document);

            xml = StringUtils.stripAccents(xml).replaceAll("[^a-zA-Z0-9 /?:().,'+<>=\"-]", " ");
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
