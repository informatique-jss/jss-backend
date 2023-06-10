package com.jss.osiris.modules.invoicing.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.azure.FormRecognizerService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.AzureInvoice;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.repository.AzureInvoiceRepository;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.service.BankTransfertService;
import com.jss.osiris.modules.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.quotation.service.DebourService;
import com.jss.osiris.modules.quotation.service.ProvisionService;

@Service
public class AzureInvoiceServiceImpl implements AzureInvoiceService {

    @Autowired
    AzureInvoiceRepository azureInvoiceRepository;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    FormRecognizerService formRecognizerService;

    @Autowired
    ConstantService constantService;

    @Autowired
    DebourService debourService;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    BankTransfertService bankTransfertService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Override
    public AzureInvoice getAzureInvoice(Integer id) {
        Optional<AzureInvoice> azureInvoice = azureInvoiceRepository.findById(id);
        if (azureInvoice.isPresent())
            return azureInvoice.get();
        return null;
    }

    @Override
    public AzureInvoice getAzureInvoiceByInvoiceId(String invoiceId) {
        return azureInvoiceRepository.findByInvoiceId(invoiceId);
    }

    @Override
    public List<AzureInvoice> searchAzureInvoicesByInvoiceId(String invoiceId) {
        return azureInvoiceRepository.findByInvoiceIdContainingAndToChekAndInvoice(invoiceId.trim());
    }

    @Override
    public AzureInvoice addOrUpdateAzureInvoice(AzureInvoice azureInvoice) {
        return azureInvoiceRepository.save(azureInvoice);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void checkInvoiceToAnalyse()
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        List<Attachment> attachments = attachmentService.getInvoiceAttachmentOnProvisionToAnalyse();
        if (attachments != null && attachments.size() > 0) {
            for (Attachment attachment : attachments)
                try {
                    formRecognizerService.recongnizeInvoice(attachment);
                } catch (Exception e) {
                    attachmentService.disableDocument(attachment);
                    throw new OsirisException(e, "Erreur while recongnize invoice with Azure");
                }
        }
        matchAzureInvoiceAndDebours();
    }

    @Override
    public List<AzureInvoice> getAzureInvoices(Boolean displayOnlyToCheck) {
        if (displayOnlyToCheck)
            return azureInvoiceRepository.findByToCheck(displayOnlyToCheck);
        return IterableUtils.toList(azureInvoiceRepository.findAll());
    }

    private void matchAzureInvoiceAndDebours()
            throws OsirisClientMessageException, OsirisException, OsirisValidationException {
        List<AzureInvoice> invoices = azureInvoiceRepository
                .findInvoicesToMatch(
                        Arrays.asList(
                                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED)
                                        .getId(),
                                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BILLED)
                                        .getId()));

        if (invoices != null && invoices.size() > 0) {
            for (AzureInvoice invoice : invoices) {
                if (invoice.getToCheck() == true) {
                    invoice = formRecognizerService.checkInvoiceAmountConfidence(invoice);
                    if (invoice.getToCheck() == true)
                        continue; // We not use if we don't have confidence
                    else
                        addOrUpdateAzureInvoice(invoice); // status changed, save it
                }

                // If find in multiple provision, or if we don't have default payment for AC do
                // nothing...
                if (invoice.getAttachments() != null && invoice.getAttachments().size() == 1
                        && invoice.getCompetentAuthority().getDefaultPaymentType() != null) {
                    Provision provision = invoice.getAttachments().get(0).getProvision();

                    if (invoice.getReference() == null) {
                        // Cannot cross verify customer order, stop here
                        continue;
                    }
                    if (!invoice.getReference()
                            .contains(provision.getAssoAffaireOrder().getCustomerOrder().getId() + "")) {
                        // Cannot cross verify customer order, stop here
                        continue;
                    }

                    // Check if all debours of this AC are not attached to an invoice to delete them
                    boolean allDeboursDeletables = true;
                    if (provision.getDebours() != null && provision.getDebours().size() > 0)
                        for (Debour debour : provision.getDebours())
                            if (debour.getCompetentAuthority().getId()
                                    .equals(invoice.getCompetentAuthority().getId())) {
                                if (debour.getInvoiceItem() != null)
                                    allDeboursDeletables = false;
                                if (debour.getPaymentType().getId()
                                        .equals(constantService.getPaymentTypeVirement().getId())
                                        && debour.getBankTransfert().getIsAlreadyExported())
                                    allDeboursDeletables = false;
                                if (debour.getPaymentType().getId()
                                        .equals(constantService.getPaymentTypeEspeces().getId()))
                                    allDeboursDeletables = false;
                                if (debour.getPaymentType().getId()
                                        .equals(constantService.getPaymentTypeCheques().getId()))
                                    allDeboursDeletables = false;
                                if (debour.getPaymentType().getId()
                                        .equals(constantService.getPaymentTypeAccount().getId()))
                                    allDeboursDeletables = false;
                                if (debour.getInvoiceItem() != null)
                                    allDeboursDeletables = false;
                                if (debour.getPayment() != null)
                                    allDeboursDeletables = false;
                                if (debour.getIsAssociated() != null)
                                    allDeboursDeletables = false;
                            }

                    if (allDeboursDeletables) {
                        if (provision.getDebours() != null && provision.getDebours().size() > 0)
                            for (Debour debour : provision.getDebours())
                                if (debour.getCompetentAuthority().getId()
                                        .equals(invoice.getCompetentAuthority().getId()))
                                    deleteDebour(debour);
                        generateDeboursAndInvoiceFromInvoice(invoice, provision);
                    }
                }
            }
        }
    }

    private void deleteDebour(Debour debour) throws OsirisException {
        List<AccountingRecord> debourRecords = accountingRecordService.getAccountingRecordForDebour(debour);
        if (debourRecords != null && debourRecords.size() > 0) {
            for (AccountingRecord debourRecord : debourRecords)
                accountingRecordService.generateCounterPart(debourRecord, debour.getId(),
                        constantService.getAccountingJournalMiscellaneousOperations());
        }
        if (debour.getPaymentType().getId().equals(constantService.getPaymentTypeVirement().getId())
                && debour.getBankTransfert() != null) {
            bankTransfertService.cancelBankTransfert(debour.getBankTransfert());
        }
        debourService.deleteDebour(debour);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Invoice generateDeboursAndInvoiceFromInvoiceFromUser(AzureInvoice azureInvoice, Provision currentProvision)
            throws OsirisClientMessageException, OsirisException, OsirisValidationException {
        azureInvoice = getAzureInvoice(azureInvoice.getId());
        currentProvision = provisionService.getProvision(currentProvision.getId());
        return generateDeboursAndInvoiceFromInvoice(azureInvoice, currentProvision);
    }

    private Invoice generateDeboursAndInvoiceFromInvoice(AzureInvoice azureInvoice, Provision currentProvision)
            throws OsirisClientMessageException, OsirisException, OsirisValidationException {
        Debour newDebour = new Debour();
        newDebour.setBillingType(constantService.getBillingTypeEmolumentsDeGreffeDebour());
        newDebour.setComments("Créé depuis la facture " + azureInvoice.getInvoiceId());
        newDebour.setCompetentAuthority(azureInvoice.getCompetentAuthority());
        newDebour.setDebourAmount(azureInvoice.getInvoiceTotal());
        newDebour.setInvoicedAmount(azureInvoice.getInvoiceTotal());
        newDebour.setPaymentDateTime(azureInvoice.getInvoiceDate().atTime(12, 0));
        newDebour.setPaymentType(newDebour.getCompetentAuthority().getDefaultPaymentType());
        newDebour.setProvision(currentProvision);
        debourService.addOrUpdateDebour(newDebour);

        if (currentProvision.getDebours() == null)
            currentProvision.setDebours(new ArrayList<Debour>());
        currentProvision.getDebours().add(newDebour);
        provisionService.addOrUpdateProvision(currentProvision);

        Invoice invoice = new Invoice();
        invoice.setCompetentAuthority(azureInvoice.getCompetentAuthority());
        invoice.setCustomerOrderForInboundInvoice(currentProvision.getAssoAffaireOrder().getCustomerOrder());
        invoice.setManualAccountingDocumentNumber(azureInvoice.getInvoiceId());
        invoice.setIsInvoiceFromProvider(true);
        invoice.setAzureInvoice(azureInvoice);
        invoice.setManualAccountingDocumentDate(azureInvoice.getInvoiceDate());
        for (AssoAffaireOrder asso : invoice.getCustomerOrderForInboundInvoice().getAssoAffaireOrders())
            for (Provision provision : asso.getProvisions())
                if (provision.getDebours() != null && provision.getDebours().size() > 0)
                    for (Debour debour : provision.getDebours())
                        if (debour.getId().equals(newDebour.getId())) {
                            debour.setNonTaxableAmount(azureInvoice.getInvoiceNonTaxableTotal());
                        }
        invoiceService.addOrUpdateInvoiceFromUser(invoice);
        azureInvoice.getAttachments().get(0).setInvoice(invoice);
        attachmentService.addOrUpdateAttachment(azureInvoice.getAttachments().get(0));

        return invoice;
    }

    @Override
    public List<AzureInvoice> findByCompetentAuthorityAndInvoiceId(CompetentAuthority competentAuthority,
            String invoiceId) {
        return azureInvoiceRepository.findByCompetentAuthorityAndInvoiceId(competentAuthority,
                invoiceId);
    }

    @Override
    public List<AzureInvoice> findByCompetentAuthorityAndInvoiceIdContains(CompetentAuthority competentAuthority,
            String invoiceId) {
        return azureInvoiceRepository.findByCompetentAuthorityAndInvoiceIdContainingIgnoreCase(
                competentAuthority, invoiceId);
    }
}
