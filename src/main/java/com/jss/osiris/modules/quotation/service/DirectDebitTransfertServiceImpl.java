package com.jss.osiris.modules.quotation.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.libs.transfer.CdtrSchmeIdBean;
import com.jss.osiris.libs.transfer.CdtrSchmeIdBeanIdBean;
import com.jss.osiris.libs.transfer.CstmrCdtTrfInitnBean;
import com.jss.osiris.libs.transfer.CtgyPurpBean;
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
import com.jss.osiris.libs.transfer.MndtRltdInfBean;
import com.jss.osiris.libs.transfer.PmtIdBean;
import com.jss.osiris.libs.transfer.PmtInfBean;
import com.jss.osiris.libs.transfer.PmtTpInfBean;
import com.jss.osiris.libs.transfer.PrvtIdBean;
import com.jss.osiris.libs.transfer.PrvtOtherBean;
import com.jss.osiris.libs.transfer.RmtInfBean;
import com.jss.osiris.libs.transfer.SchmeNmBean;
import com.jss.osiris.libs.transfer.SvcLvlBean;
import com.jss.osiris.modules.invoicing.model.DirectDebitTransfertSearch;
import com.jss.osiris.modules.invoicing.model.DirectDebitTransfertSearchResult;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.DirectDebitTransfert;
import com.jss.osiris.modules.quotation.repository.DirectDebitTransfertRepository;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Service
public class DirectDebitTransfertServiceImpl implements DirectDebitTransfertService {

    @Autowired
    DirectDebitTransfertRepository directDebitTransfertRepository;

    @Autowired
    IndexEntityService indexEntityService;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Value("${jss.iban}")
    private String ibanJss;

    @Value("${jss.bic}")
    private String bicJss;

    @Value("${jss.sepa.identification}")
    private String jssSepaIdentification;

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
            DirectDebitTransfert directDebitTransfert) {
        return directDebitTransfertRepository.save(directDebitTransfert);
    }

    @Override
    public void reindexDirectDebitTransfert() {
        List<DirectDebitTransfert> directDebitTransferts = getDirectDebitTransferts();
        if (directDebitTransferts != null)
            for (DirectDebitTransfert directDebitTransfert : directDebitTransferts)
                indexEntityService.indexEntity(directDebitTransfert, directDebitTransfert.getId());
    }

    @Override
    public List<DirectDebitTransfertSearchResult> searchDirectDebitTransfert(
            DirectDebitTransfertSearch directDebitTransfertSearch) {
        if (directDebitTransfertSearch.getStartDate() == null)
            directDebitTransfertSearch.setStartDate(LocalDateTime.now().minusYears(100));
        if (directDebitTransfertSearch.getEndDate() == null)
            directDebitTransfertSearch.setEndDate(LocalDateTime.now().plusYears(100));
        return directDebitTransfertRepository.findTransferts(
                directDebitTransfertSearch.getStartDate().withHour(0).withMinute(0),
                directDebitTransfertSearch.getEndDate().withHour(23).withMinute(59),
                directDebitTransfertSearch.getMinAmount(),
                directDebitTransfertSearch.getMaxAmount(),
                directDebitTransfertSearch.getLabel(), directDebitTransfertSearch.isHideExportedDirectDebitTransfert());
    }

    @Override
    public DirectDebitTransfert cancelDirectDebitTransfert(DirectDebitTransfert directDebitTransfert) {
        directDebitTransfert.setIsCancelled(true);
        return addOrUpdateDirectDebitTransfert(directDebitTransfert);
    }

    @Override
    public DirectDebitTransfert generateDirectDebitTransfertForOutboundInvoice(Invoice invoice)
            throws OsirisException, OsirisClientMessageException {

        ITiers tiers = invoiceHelper.getCustomerOrder(invoice);
        String sepaReference = null;
        LocalDate sepaDate = null;
        String customerOrderLabel = "";
        if (tiers instanceof Responsable) {
            sepaReference = ((Responsable) tiers).getTiers().getSepaMandateReference();
            sepaDate = ((Responsable) tiers).getTiers().getSepaMandateSignatureDate();
            customerOrderLabel = ((Responsable) tiers).getTiers().getDenomination() != null
                    ? ((Responsable) tiers).getTiers().getDenomination()
                    : ((Responsable) tiers).getTiers().getFirstname() + " "
                            + ((Responsable) tiers).getTiers().getLastname();
        } else if (tiers instanceof Tiers) {
            sepaReference = ((Tiers) tiers).getSepaMandateReference();
            sepaDate = ((Tiers) tiers).getSepaMandateSignatureDate();
            customerOrderLabel = ((Tiers) tiers).getDenomination() != null
                    ? ((Tiers) tiers).getDenomination()
                    : ((Tiers) tiers).getFirstname() + " "
                            + ((Tiers) tiers).getLastname();
        } else if (tiers instanceof Confrere) {
            sepaReference = ((Confrere) tiers).getSepaMandateReference();
            sepaDate = ((Confrere) tiers).getSepaMandateSignatureDate();
            customerOrderLabel = ((Confrere) tiers).getLabel();
        }

        if (sepaReference == null)
            throw new OsirisClientMessageException("Référence du mandat SEPA non trouvée sur le donneur d'ordre");

        if (sepaDate == null)
            throw new OsirisClientMessageException("Date du mandat SEPA non trouvée sur le donneur d'ordre");

        DirectDebitTransfert directDebitTransfert = new DirectDebitTransfert();
        directDebitTransfert.setLabel("Facture " + invoice.getId() + " / Journal Spécial des Sociétés / "
                + (invoice.getCustomerOrder() != null ? invoice.getCustomerOrder().getId() : ""));
        directDebitTransfert.setIsAlreadyExported(false);
        directDebitTransfert.setTransfertAmount(invoice.getTotalPrice());
        directDebitTransfert.setTransfertDateTime(invoice.getDueDate().atTime(12, 0));
        directDebitTransfert.setTransfertIban(invoiceHelper.getIbanOfOrderingCustomer(invoice));
        directDebitTransfert.setTransfertBic(invoiceHelper.getBicOfOrderingCustomer(invoice));

        if (directDebitTransfert.getTransfertIban() == null || directDebitTransfert.getTransfertBic() == null)
            throw new OsirisException(null, "IBAN or BIC not found for direct debit transfert");

        directDebitTransfert.setTransfertIban(directDebitTransfert.getTransfertIban().replaceAll(" ", ""));
        directDebitTransfert.setTransfertBic(directDebitTransfert.getTransfertBic().replaceAll(" ", ""));

        directDebitTransfert.setSepaMandateReference(sepaReference);
        directDebitTransfert.setSepaMandateSignatureDate(sepaDate);
        directDebitTransfert.setIsCancelled(false);
        directDebitTransfert.setCustomerOrderLabel(customerOrderLabel);
        return this.addOrUpdateDirectDebitTransfert(directDebitTransfert);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public File getDirectDebitTransfertExport(DirectDebitTransfertSearch transfertSearch) throws OsirisException {

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
            document.setCstmrCdtTrfInitnBean(new CstmrCdtTrfInitnBean());

            GrpHdrBean header = new GrpHdrBean();
            document.getCstmrCdtTrfInitnBean().setGrpHdrBean(header);

            header.setMsgId("Prélèvements JSS du " + LocalDateTime.now().format(formatterDate));
            header.setCreDtTm(LocalDateTime.now().format(formatterDateTime));
            header.setNbOfTxs(bankTransferts.size());
            header.setCtrlSum(Math.round(totalAmount * 100f) / 100f);

            InitgPtyBean emiterDetails = new InitgPtyBean();
            header.setInitgPtyBean(emiterDetails);
            emiterDetails.setNm("JOURNAL SPECIAL DES SOCIETES");

            document.getCstmrCdtTrfInitnBean().setPmtInfBean(new ArrayList<PmtInfBean>());

            for (DirectDebitTransfertSearchResult bankTransfert : bankTransferts) {

                PmtInfBean body = new PmtInfBean();
                document.getCstmrCdtTrfInitnBean().getPmtInfBean().add(body);
                body.setPmtInfId(bankTransfert.getTransfertLabel());
                body.setPmtMtd("DD");
                body.setBtchBookg(false);
                body.setNbOfTxs(bankTransferts.size());
                body.setCtrlSum(Math.round(totalAmount * 100f) / 100f);

                PmtTpInfBean bodyTransfertType = new PmtTpInfBean();

                SvcLvlBean transfertNorm = new SvcLvlBean();
                bodyTransfertType.setSvcLvlBean(transfertNorm);
                transfertNorm.setCd("SEPA");

                CtgyPurpBean transfertPurpose = new CtgyPurpBean();
                bodyTransfertType.setLclInstrmBean(transfertPurpose);
                transfertPurpose.setCd("CORE");

                bodyTransfertType.setSeqTp("RCUR");

                body.setReqdColltnDt(bankTransfert.getTransfertDate().format(formatterDate));

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

                body.setChrgBr("SLEV");

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
                bicVirement.setFinInstnIdBean(financialInstitution);
                bicVirementId.setBic(bicJss);

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
                virementLabel.setUstrd(StringUtils.substring(completeTransfert.getLabel(), 0, 139));

                body.getDrctDbtTxInfBeanList().add(prelevement);

                completeTransfert.setIsAlreadyExported(true);
                addOrUpdateDirectDebitTransfert(completeTransfert);
            }

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
