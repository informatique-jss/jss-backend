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
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.AzureInvoice;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.repository.AzureInvoiceRepository;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
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
    public AzureInvoice addOrUpdateAzureInvoice(AzureInvoice azureInvoice) {
        return azureInvoiceRepository.save(azureInvoice);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void checkInvoiceToAnalyse() throws OsirisException, OsirisClientMessageException {
        List<Attachment> attachments = attachmentService.getInvoiceAttachmentOnProvisionToAnalyse();
        if (attachments != null && attachments.size() > 0) {
            for (Attachment attachment : attachments)
                if (attachment.getId().equals(5826951)) // TODO remove
                    formRecognizerService.recongnizeInvoice(attachment);
        }
        matchAzureInvoiceAndDebours();
    }

    @Override
    public List<AzureInvoice> getAzureInvoices(Boolean displayOnlyToCheck) {
        if (displayOnlyToCheck)
            return azureInvoiceRepository.findByToCheck(displayOnlyToCheck);
        return IterableUtils.toList(azureInvoiceRepository.findAll());
    }

    private void matchAzureInvoiceAndDebours() throws OsirisClientMessageException, OsirisException {
        List<AzureInvoice> invoices = azureInvoiceRepository
                .findInvoicesToMatch(
                        Arrays.asList(
                                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED)
                                        .getId(),
                                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BILLED)
                                        .getId()));

        if (invoices != null && invoices.size() > 0) {
            for (AzureInvoice invoice : invoices) {
                // If find in multiple provision, or if we don't have default payment for AC do
                // nothing...
                if (invoice.getAttachments() != null && invoice.getAttachments().size() == 1
                        && invoice.getCompetentAuthority().getDefaultPaymentType() != null) {
                    Provision provision = invoice.getAttachments().get(0).getProvision();

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

    private Invoice generateDeboursAndInvoiceFromInvoice(AzureInvoice azureInvoice, Provision currentProvision)
            throws OsirisClientMessageException, OsirisException {
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
}