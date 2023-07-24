package com.jss.osiris.modules.invoicing.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.azure.FormRecognizerService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.AzureInvoice;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.repository.AzureInvoiceRepository;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.service.BankTransfertService;
import com.jss.osiris.modules.quotation.service.CustomerOrderStatusService;
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
                    throw new OsirisException(e,
                            "Erreur while recongnize invoice with Azure for attachment " + attachment.getId());
                }
        }
        matchAzureInvoiceAndDebours();
    }

    @Override
    public List<AzureInvoice> getAzureInvoices(Boolean displayOnlyToCheck) {
        if (displayOnlyToCheck)
            return azureInvoiceRepository.findTop100ByToCheckAndIsDisabled(displayOnlyToCheck, false);
        return azureInvoiceRepository.findByIsDisabled(false);
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

                }
            }
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Invoice generateDeboursAndInvoiceFromInvoiceFromUser(AzureInvoice azureInvoice, Provision currentProvision)
            throws OsirisClientMessageException, OsirisException, OsirisValidationException {
        azureInvoice = getAzureInvoice(azureInvoice.getId());
        currentProvision = provisionService.getProvision(currentProvision.getId());
        return generateInvoiceFromInvoice(azureInvoice, currentProvision);
    }

    private Invoice generateInvoiceFromInvoice(AzureInvoice azureInvoice, Provision currentProvision)
            throws OsirisClientMessageException, OsirisException, OsirisValidationException {

        if (azureInvoice.getCompetentAuthority().getDefaultPaymentType() == null)
            throw new OsirisClientMessageException(
                    "Type de paiement par défaut non renseigné sur l'autorité compétente");

        Invoice invoice = new Invoice();
        invoice.setCompetentAuthority(azureInvoice.getCompetentAuthority());
        invoice.setCustomerOrderForInboundInvoice(currentProvision.getAssoAffaireOrder().getCustomerOrder());
        invoice.setManualAccountingDocumentNumber(azureInvoice.getInvoiceId());
        invoice.setIsInvoiceFromProvider(true);
        invoice.setAzureInvoice(azureInvoice);
        invoice.setManualAccountingDocumentDate(azureInvoice.getInvoiceDate());
        // TODO : setter le montant refacturé
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
