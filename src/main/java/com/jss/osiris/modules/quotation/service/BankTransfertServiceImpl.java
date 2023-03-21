package com.jss.osiris.modules.quotation.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
import com.jss.osiris.modules.invoicing.model.BankTransfertSearch;
import com.jss.osiris.modules.invoicing.model.BankTransfertSearchResult;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.BankTransfert;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.repository.BankTransfertRepository;

@Service
public class BankTransfertServiceImpl implements BankTransfertService {

    @Autowired
    BankTransfertRepository bankTransfertRepository;

    @Autowired
    IndexEntityService indexEntityService;

    @Value("${jss.iban}")
    private String ibanJss;

    @Value("${jss.bic}")
    private String bicJss;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    ProvisionService provisionService;

    @Override
    public List<BankTransfert> getBankTransfers() {
        return IterableUtils.toList(bankTransfertRepository.findAll());
    }

    @Override
    public BankTransfert getBankTransfert(Integer id) {
        Optional<BankTransfert> bankTransfert = bankTransfertRepository.findById(id);
        if (bankTransfert.isPresent())
            return bankTransfert.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BankTransfert addOrUpdateBankTransfert(
            BankTransfert bankTransfert) {
        bankTransfert = bankTransfertRepository.save(bankTransfert);
        indexEntityService.indexEntity(bankTransfert, bankTransfert.getId());
        return bankTransfert;
    }

    @Override
    public BankTransfert cancelBankTransfert(BankTransfert bankTransfert) {
        bankTransfert.setIsCancelled(true);
        return addOrUpdateBankTransfert(bankTransfert);
    }

    @Override
    public BankTransfert selectBankTransfertForExport(BankTransfert bankTransfert, boolean isSelected) {
        bankTransfert.setIsSelectedForExport(isSelected);
        return addOrUpdateBankTransfert(bankTransfert);
    }

    @Override
    public void reindexBankTransfert() {
        List<BankTransfert> bankTransferts = getBankTransfers();
        if (bankTransferts != null)
            for (BankTransfert bankTransfert : bankTransferts)
                indexEntityService.indexEntity(bankTransfert, bankTransfert.getId());
    }

    @Override
    public List<BankTransfertSearchResult> searchBankTransfert(BankTransfertSearch bankTransfertSearch) {
        if (bankTransfertSearch.getStartDate() == null)
            bankTransfertSearch.setStartDate(LocalDateTime.now().minusYears(100));
        if (bankTransfertSearch.getEndDate() == null)
            bankTransfertSearch.setEndDate(LocalDateTime.now().plusYears(100));
        return bankTransfertRepository.findTransferts(
                bankTransfertSearch.getStartDate().withHour(0).withMinute(0),
                bankTransfertSearch.getEndDate().withHour(23).withMinute(59), bankTransfertSearch.getMinAmount(),
                bankTransfertSearch.getMaxAmount(),
                bankTransfertSearch.getLabel(), bankTransfertSearch.isHideExportedBankTransfert(),
                bankTransfertSearch.isDisplaySelectedForExportBankTransfert());
    }

    @Override
    public BankTransfert generateBankTransfertForDebour(Debour debour, AssoAffaireOrder asso,
            CustomerOrder customerOrder, LocalDate dueDate)
            throws OsirisException, OsirisClientMessageException {

        if (debour.getCompetentAuthority().getIban() == null || debour.getCompetentAuthority().getIban().equals(""))
            throw new OsirisClientMessageException(
                    "IBAN non renseigné pour l'autorité compétente " + debour.getCompetentAuthority().getLabel());

        if (debour.getCompetentAuthority().getBic() == null || debour.getCompetentAuthority().getBic().equals(""))
            throw new OsirisClientMessageException(
                    "BIC non renseigné pour l'autorité compétente " + debour.getCompetentAuthority().getLabel());

        BankTransfert bankTransfert = new BankTransfert();
        bankTransfert.setLabel("Débours " + debour.getId() + " / JSS / " + customerOrder.getId() + " / "
                + (asso.getAffaire().getDenomination() == null
                        ? asso.getAffaire().getFirstname() + " " + asso.getAffaire().getLastname()
                        : asso.getAffaire().getDenomination()));
        bankTransfert.setIsAlreadyExported(false);
        bankTransfert.setTransfertAmount(debour.getDebourAmount());
        bankTransfert.setTransfertDateTime(dueDate.atTime(12, 0));
        bankTransfert.setTransfertIban(debour.getCompetentAuthority().getIban());
        bankTransfert.setTransfertBic(debour.getCompetentAuthority().getBic());
        bankTransfert.setTransfertIban(bankTransfert.getTransfertIban().replaceAll(" ", ""));
        bankTransfert.setTransfertBic(bankTransfert.getTransfertBic().replaceAll(" ", ""));
        bankTransfert.setIsCancelled(false);
        return this.addOrUpdateBankTransfert(bankTransfert);
    }

    @Override
    public BankTransfert generateBankTransfertForManualInvoice(Invoice invoice)
            throws OsirisException, OsirisClientMessageException {
        BankTransfert bankTransfert = new BankTransfert();
        bankTransfert.setLabel("Facture " + invoice.getId() + " / JSS / "
                + (invoice.getCommandNumber() != null ? invoice.getCommandNumber() : "") + " / "
                + invoice.getManualAccountingDocumentNumber());
        bankTransfert.setIsAlreadyExported(false);
        bankTransfert.setTransfertAmount(invoice.getTotalPrice());
        bankTransfert.setTransfertDateTime(invoice.getDueDate().atTime(12, 0));
        bankTransfert.setTransfertIban(invoiceHelper.getIbanOfOrderingCustomer(invoice));
        bankTransfert.setTransfertBic(invoiceHelper.getBicOfOrderingCustomer(invoice));

        if (bankTransfert.getTransfertIban() == null || bankTransfert.getTransfertBic() == null)
            throw new OsirisException(null, "IBAN or BIC not found for bank transfert");

        bankTransfert.setTransfertIban(bankTransfert.getTransfertIban().replaceAll(" ", ""));
        bankTransfert.setTransfertBic(bankTransfert.getTransfertBic().replaceAll(" ", ""));
        bankTransfert.setIsCancelled(false);

        return this.addOrUpdateBankTransfert(bankTransfert);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public File getBankTransfertExport(BankTransfertSearch transfertSearch) throws OsirisException {
        transfertSearch.setDisplaySelectedForExportBankTransfert(true);
        List<BankTransfertSearchResult> bankTransferts = searchBankTransfert(transfertSearch);
        String xml = "";

        if (bankTransferts == null || bankTransferts.size() == 0)
            return null;

        Float totalAmount = 0f;
        for (BankTransfertSearchResult bankTransfert : bankTransferts)
            totalAmount += Math.round(bankTransfert.getTransfertAmount() * 100f) / 100f;

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            DocumentBean document = new DocumentBean();
            document.setCstmrCdtTrfInitnBean(new CstmrCdtTrfInitnBean());

            GrpHdrBean header = new GrpHdrBean();
            document.getCstmrCdtTrfInitnBean().setGrpHdrBean(header);

            header.setMsgId(("Virement JSS du " + LocalDateTime.now().format(formatterDate)));
            header.setCreDtTm(LocalDateTime.now().format(formatterDateTime));
            header.setNbOfTxs(bankTransferts.size());
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

            document.getCstmrCdtTrfInitnBean().setPmtInfBean(new ArrayList<PmtInfBean>());

            for (BankTransfertSearchResult bankTransfert : bankTransferts) {
                BankTransfert completeTransfert = getBankTransfert(bankTransfert.getId());

                document.getCstmrCdtTrfInitnBean().getPmtInfBean().add(generateBodyForBankTransfert(
                        header.getMsgId(), bankTransfert.getTransfertAmount(),
                        bankTransfert.getTransfertDate().toLocalDate(),
                        (bankTransfert.getCompetentAuthorityLabel() != null ? bankTransfert.getCompetentAuthorityLabel()
                                : StringUtils.substring(bankTransfert.getInvoiceBillingLabel(), 0, 139)),
                        completeTransfert.getTransfertIban().replaceAll(" ", ""),
                        completeTransfert.getTransfertBic().replaceAll(" ", ""),
                        StringUtils.substring(completeTransfert.getLabel(), 0, 139)));

                if (completeTransfert.getIsAlreadyExported() == false && completeTransfert.getDebours() != null
                        && completeTransfert.getDebours().size() > 0) {
                    for (Debour debour : completeTransfert.getDebours()) {
                        Provision provision = provisionService.getProvision(debour.getProvision().getId());
                        accountingRecordService.generateBankAccountingRecordsForOutboundDebourPayment(debour,
                                provision.getAssoAffaireOrder().getCustomerOrder());
                    }
                }

                completeTransfert.setIsAlreadyExported(true);
                addOrUpdateBankTransfert(completeTransfert);
            }

            xml = xmlMapper.writeValueAsString(document);

            // Non supporting characters for bank ...
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

    @Override
    public PmtInfBean generateBodyForBankTransfert(String headerLabel, Float transfertAmount, LocalDate executionDate,
            String recipientLabel,
            String iban, String transfertBic, String transfertLabel) {
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        PmtInfBean body = new PmtInfBean();

        body.setPmtInfId(headerLabel);
        body.setPmtMtd("TRF");
        body.setBtchBookg(false);
        body.setNbOfTxs(1);
        body.setCtrlSum(transfertAmount);

        PmtTpInfBean bodyTransfertType = new PmtTpInfBean();
        body.setPmtTpInfBean(bodyTransfertType);
        bodyTransfertType.setInstrPrty("NORM");

        SvcLvlBean transfertNorm = new SvcLvlBean();
        bodyTransfertType.setSvcLvlBean(transfertNorm);
        transfertNorm.setCd("SEPA");

        CtgyPurpBean transfertPurpose = new CtgyPurpBean();
        bodyTransfertType.setCtgyPurpBean(transfertPurpose);
        transfertPurpose.setCd("FOUR");

        body.setReqdExctnDt(executionDate.format(formatterDate));

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

        CdtTrfTxInfBean virement = new CdtTrfTxInfBean();
        PmtIdBean virementId = new PmtIdBean();
        virement.setPmtIdBean(virementId);
        virementId.setEndToEndId("2-Virement JSS du " + LocalDateTime.now().format(formatterDate));
        virementId.setInstrId("2-Virement JSS du " + LocalDateTime.now().format(formatterDate));

        AmtBean currency = new AmtBean();
        virement.setAmtBean(currency);
        InstdAmtBean currencyDetails = new InstdAmtBean();
        currencyDetails.setCcy("EUR");
        currencyDetails.setValue(Math.round(transfertAmount * 100f) / 100f + "");
        currency.setInstdAmtBean(currencyDetails);

        CdtrAgtBean bicVirement = new CdtrAgtBean();
        virement.setCdtrAgtBeanList(Arrays.asList(bicVirement));
        FinInstnIdBean bicVirementId = new FinInstnIdBean();
        bicVirement.setFinInstnIdBean(bicVirementId);
        bicVirementId.setBic(transfertBic);

        CdtrBean customerOrder = new CdtrBean();
        virement.setCdtrBean(customerOrder);
        customerOrder.setNm(recipientLabel);

        CdtrAcctBean customerAccount = new CdtrAcctBean();
        virement.setCdtrAcctBean(customerAccount);
        IdBean customerAccountId = new IdBean();
        customerAccount.setIdBean(customerAccountId);
        customerAccountId.setIban(iban);

        RmtInfBean virementLabel = new RmtInfBean();
        virement.setRmtInfBean(virementLabel);
        virementLabel.setUstrd(transfertLabel);

        body.getCdtTrfTxInfBeanList().add(virement);
        return body;
    }
}
