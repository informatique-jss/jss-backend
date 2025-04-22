package com.jss.osiris.modules.osiris.invoicing.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.GeneratePdfDelegate;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.osiris.accounting.service.AccountingRecordGenerationService;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceSearch;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.repository.InvoiceRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Provider;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.osiris.miscellaneous.service.VatService;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.service.BankTransfertService;
import com.jss.osiris.modules.osiris.quotation.service.ConfrereService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.DirectDebitTransfertService;
import com.jss.osiris.modules.osiris.quotation.service.PricingHelper;
import com.jss.osiris.modules.osiris.tiers.model.BillingLabelType;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    DocumentService documentService;

    @Autowired
    InvoiceStatusService invoiceStatusService;

    @Autowired
    ConstantService constantService;

    @Autowired
    AccountingRecordGenerationService accountingRecordGenerationService;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    MailHelper mailHelper;

    @Autowired
    NotificationService notificationService;

    @Autowired
    GeneratePdfDelegate generatePdfDelegate;

    @Autowired
    TiersService tiersService;

    @Autowired
    ConfrereService confrereService;

    @Autowired
    BankTransfertService bankTransfertService;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    PricingHelper pricingHelper;

    @Autowired
    BillingItemService billingItemService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    VatService vatService;

    @Autowired
    DirectDebitTransfertService directDebitTransfertService;

    @Autowired
    BatchService batchService;

    @Override
    public List<Invoice> getAllInvoices() {
        return IterableUtils.toList(invoiceRepository.findAll());
    }

    @Override
    public Invoice searchInvoicesByIdDirectDebitTransfert(Integer idToFind) {
        return invoiceRepository.searchInvoicesByIdDirectDebitTransfert(idToFind);
    }

    @Override
    public Invoice getInvoice(Integer id) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (invoice.isPresent())
            return invoice.get();
        return null;
    }

    @Override
    public void deleteDuplicateInvoices() {
        invoiceRepository.deleteDuplicateInvoices();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Invoice addOrUpdateInvoiceFromUser(Invoice invoice)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        if (!hasAtLeastOneInvoiceItemNotNull(invoice))
            throw new OsirisException(null, "No invoice item found on invoice");

        if (invoice.getId() != null)
            throw new OsirisClientMessageException("Impossible de modifier une facture");

        // check if AccountingDocumentNumber already exists for a provider, only for new
        // invoice
        if (invoice.getId() == null && invoice.getManualAccountingDocumentNumber() != null
                && invoice.getProvider() != null && !invoice.getProvider().getId()
                        .equals(constantService.getCompetentAuthorityInpi().getProvider().getId())) {
            List<Invoice> duplicateInvoices = invoiceRepository
                    .findByProviderAndManualAccountingDocumentNumberIgnoreCase(invoice.getProvider(),
                            invoice.getManualAccountingDocumentNumber());
            if (duplicateInvoices != null && duplicateInvoices.size() > 0)
                throw new OsirisValidationException("N° de pièce comptable existante");
        }

        // Define booleans
        if (invoice.getIsCreditNote() == null)
            invoice.setIsCreditNote(false);

        // Define due date
        if (invoice.getDueDate() == null)
            if (invoice.getResponsable() != null)
                invoice.setDueDate(getDueDateForInvoice(invoice.getResponsable(), invoice));
            else if (invoice.getProvider() != null) {
                if (invoice.getManualAccountingDocumentDate() != null)
                    invoice.setDueDate(invoice.getManualAccountingDocumentDate().plusDays(30));
                else if (invoice.getCreatedDate() != null)
                    invoice.setDueDate(invoice.getCreatedDate().plusDays(30).toLocalDate());
                else
                    invoice.setDueDate(LocalDate.now().plusDays(30));
            }

        invoice.setCreatedDate(LocalDateTime.now());

        // Defined billing label
        // If it's a credit note, no need, label is taken from invoice clone
        if (invoice.getRff() == null && invoice.getResponsable() != null && !invoice.getIsCreditNote()
                && (invoice.getBillingLabelType() == null || !constantService.getBillingLabelTypeOther().getId()
                        .equals(invoice.getBillingLabelType().getId()))) {
            Document billingDocument = null;

            if (invoice.getCustomerOrder() != null)
                billingDocument = documentService.getBillingDocument(invoice.getCustomerOrder().getDocuments());
            else
                billingDocument = documentService.getBillingDocument(invoice.getResponsable().getDocuments());

            if (billingDocument == null)
                throw new OsirisException(null, "Billing document not found for ordering customer provided");

            invoice.setBillingLabelType(billingDocument.getBillingLabelType());
            invoiceHelper.setInvoiceLabel(invoice, billingDocument, invoice.getCustomerOrder(),
                    invoice.getResponsable());
        }

        // Define status
        if (invoice.getProvider() != null && invoice.getIsCreditNote())
            invoice.setInvoiceStatus(constantService.getInvoiceStatusCreditNoteReceived());
        else if (invoice.getProvider() != null || invoice.getRff() != null)
            invoice.setInvoiceStatus(constantService.getInvoiceStatusReceived());
        else if (invoice.getInvoiceStatus() == null)
            invoice.setInvoiceStatus(constantService.getInvoiceStatusSend());

        // Save before to have an ID on invoice
        addOrUpdateInvoice(invoice);

        // Associate invoice to invoice item
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            // If we got an AC with direct charge, force no VAT
            // if (invoice.getProvider() != null && invoice.getCompetentAuthority() != null
            // &&
            // invoice.getCompetentAuthority().getCompetentAuthorityType().getIsDirectCharge())
            // invoiceItem.setVat(constantService.getVatZero());
            // TODO refonte

            vatService.completeVatOnInvoiceItem(invoiceItem, invoice);
            invoiceItem.setInvoice(invoice);
            invoiceItemService.addOrUpdateInvoiceItem(invoiceItem);
        }

        invoiceHelper.setPriceTotal(invoice);

        // Generate accounting records
        if (invoice.getProvider() != null && invoice.getIsCreditNote())
            accountingRecordGenerationService.generateAccountingRecordsOnCreditNoteReception(invoice,
                    invoice.getReverseCreditNote());
        else if (invoice.getProvider() != null || invoice.getRff() != null)
            accountingRecordGenerationService.generateAccountingRecordsOnInvoiceReception(invoice);
        else if (invoice.getIsCreditNote())
            accountingRecordGenerationService
                    .generateAccountingRecordsOnInvoiceEmissionCancellation(invoice.getReverseCreditNote(), invoice);
        else
            accountingRecordGenerationService.generateAccountingRecordsOnInvoiceEmission(invoice);

        // Define payment type
        if (invoice.getManualPaymentType() == null) {
            PaymentType paymentType = null;
            if (invoice.getProvider() != null) {
                paymentType = invoice.getProvider().getPaymentType();
            } else {
                paymentType = constantService.getPaymentTypeVirement();
                if (invoice.getResponsable() != null && invoice.getResponsable().getTiers().getPaymentType() != null)
                    paymentType = invoice.getResponsable().getTiers().getPaymentType();
            }
            invoice.setManualPaymentType(paymentType);
        }

        // Handle provider and customer payment
        if ((invoice.getProvider() != null || invoice.getRff() != null)
                && (invoice.getIsCreditNote() == null || invoice.getIsCreditNote() == false)) {
            if (invoice.getManualPaymentType().getId().equals(constantService.getPaymentTypeVirement().getId())) {
                invoice.setBankTransfert(bankTransfertService.generateBankTransfertForManualInvoice(invoice));
            }

            if (invoice.getManualPaymentType().getId().equals(constantService.getPaymentTypeAccount().getId())) {
                Payment payment = paymentService.generateNewAccountPayment(invoice.getTotalPrice().negate(),
                        invoice.getProvider().getAccountingAccountDeposit(),
                        invoice.getProvider().getAccountingAccountProvider(),
                        "Paiement pour la facture " + invoice.getId() + " / Fournisseur : "
                                + invoice.getProvider().getLabel());
                accountingRecordGenerationService.generateAccountingRecordOnOutgoingPaymentCreation(payment, false);
                paymentService.manualMatchPaymentInvoicesAndCustomerOrders(payment, Arrays.asList(invoice), null, null,
                        null, null, null);
            }
        } else {
            if (invoice.getManualPaymentType().getId().equals(constantService.getPaymentTypePrelevement().getId())
                    && !invoice.getIsCreditNote()) {
                invoice.setDirectDebitTransfert(
                        directDebitTransfertService.generateDirectDebitTransfertForOutboundInvoice(invoice));
            }
        }

        addOrUpdateInvoice(invoice);
        if (invoice.getResponsable() != null && invoice.getRff() == null)
            generateInvoicePdf(invoice, invoice.getCustomerOrder());

        // Associate attachment for azure invoice
        if (invoice.getAzureInvoice() != null) {
            Attachment attachment = attachmentService
                    .getAttachment(invoice.getAzureInvoice().getAttachments().get(0).getId());
            attachment.setInvoice(invoice);
            attachmentService.addOrUpdateAttachment(attachment);
        }

        // If it's a provider invoice on a customer order, resave the customer order to
        // regenerate reinvoiced invoice items
        if (invoice.getProvider() != null && invoice.getCustomerOrder() != null)
            customerOrderService.addOrUpdateCustomerOrder(
                    customerOrderService.getCustomerOrder(invoice.getCustomerOrder().getId()), true, false);

        return invoice;
    }

    public Invoice generateInvoicePdf(Invoice invoice, CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        // Create invoice PDF and attach it to customerOrder and invoice
        File invoicePdf = generatePdfDelegate.generateInvoicePdf(customerOrder, invoice,
                invoice.getReverseCreditNote());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
        try {
            List<Attachment> attachments = new ArrayList<Attachment>();
            if (customerOrder != null)
                attachments = attachmentService.addAttachment(new FileInputStream(invoicePdf),
                        customerOrder.getId(), null,
                        CustomerOrder.class.getSimpleName(),
                        constantService.getAttachmentTypeInvoice(),
                        "Invoice_" + invoice.getId() + "_" + formatter.format(LocalDateTime.now()) + ".pdf",
                        false, "Facture n°" + invoice.getId(), null, null, null);
            else
                attachments = attachmentService.addAttachment(new FileInputStream(invoicePdf),
                        invoice.getId(), null,
                        Invoice.class.getSimpleName(),
                        constantService.getAttachmentTypeInvoice(),
                        "Invoice_" + invoice.getId() + "_" + formatter.format(LocalDateTime.now()) + ".pdf",
                        false, "Facture n°" + invoice.getId(), null, null, null);

            for (Attachment attachment : attachments)
                if (attachment.getDescription() != null && attachment.getDescription().contains(invoice.getId() + "")) {
                    attachment.setInvoice(invoice);
                    attachmentService.addOrUpdateAttachment(attachment);
                }

        } catch (FileNotFoundException e) {
            throw new OsirisException(e, "Impossible to read invoice PDF temp file");
        } finally {
            invoicePdf.delete();
        }
        return invoice;
    }

    private LocalDate getDueDateForInvoice(Responsable customerOrder, Invoice invoice)
            throws OsirisException, OsirisClientMessageException {
        Integer nbrOfDayFromDueDate = 30;

        if (customerOrder != null) {
            Document dunningDocument = documentService.getDocumentByDocumentType(
                    customerOrder.getTiers().getDocuments(),
                    constantService.getDocumentTypeDunning());

            if (dunningDocument == null)
                throw new OsirisClientMessageException("Merci de remplir la partie réglement pour le donneur d'ordre");

            if (dunningDocument.getPaymentDeadlineType() != null)
                nbrOfDayFromDueDate = dunningDocument.getPaymentDeadlineType().getNumberOfDay();
        }

        return LocalDate.now().plusDays(nbrOfDayFromDueDate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Invoice cancelInvoice(Invoice invoice)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        invoice = getInvoice(invoice.getId());

        if (invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId())
                || invoice.getResponsable() != null)
            return cancelInvoiceEmitted(invoice, invoice.getCustomerOrder());
        if (invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusReceived().getId())
                || invoice.getInvoiceStatus().getId()
                        .equals(constantService.getInvoiceStatusCreditNoteReceived().getId())
                || invoice.getProvider() != null
                        && invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusPayed().getId()))
            return cancelInvoiceReceived(invoice);
        return invoice;
    }

    private Invoice cancelInvoiceEmitted(Invoice invoice, CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        // Refresh invoice
        invoice = getInvoice(invoice.getId());

        if (invoice.getDirectDebitTransfert() != null) {
            if (invoice.getDirectDebitTransfert().getIsAlreadyExported() == false)
                directDebitTransfertService.cancelDirectDebitTransfert(invoice.getDirectDebitTransfert());
            else {
                if (invoice.getPayments() != null) {
                    for (Payment payment : invoice.getPayments())
                        if (payment.getPaymentType().getId()
                                .equals(constantService.getPaymentTypePrelevement().getId())) {
                            Tiers tiersInvoice = invoice.getResponsable().getTiers();
                            paymentService.refundPayment(payment, tiersInvoice, null);
                        }
                }
            }
        }

        if (invoice.getPayments() != null) {
            ArrayList<Payment> payments = new ArrayList<Payment>();
            payments.addAll(invoice.getPayments());
            for (Payment payment : payments)
                if (!payment.getIsCancelled()) {
                    if (payment.getIsAppoint())
                        paymentService.cancelAppoint(payment);
                    else if (customerOrder != null)
                        paymentService.movePaymentFromInvoiceToCustomerOrder(payment, invoice, customerOrder);
                    else
                        paymentService.unassociateInboundPaymentFromInvoice(payment, invoice);
                }
        }

        // Unlink invoice item from customer order
        if (invoice.getInvoiceItems() != null) {
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                invoiceItem.setProvision(null);
                invoiceItemService.addOrUpdateInvoiceItem(invoiceItem);
            }
        }

        invoice = getInvoice(invoice.getId());

        // Create credit note
        Invoice creditNote = cloneInvoice(invoice);
        if (invoice.getInvoiceItems() != null) {
            ArrayList<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                InvoiceItem newInvoiceItem = invoiceItemService.cloneInvoiceItem(invoiceItem);
                invoiceItems.add(newInvoiceItem);
            }
            creditNote.setInvoiceItems(invoiceItems);
            creditNote.setInvoiceStatus(constantService.getInvoiceStatusCreditNoteEmited());
            creditNote.setIsCreditNote(true);
            creditNote.setReverseCreditNote(invoice);
        }
        creditNote = addOrUpdateInvoiceFromUser(creditNote);

        invoice.setCreditNote(creditNote);
        invoice = addOrUpdateInvoice(invoice);

        if (customerOrder != null) {
            // Generate PDF and attached it to customer order
            File creditNotePdf = generatePdfDelegate.generateInvoicePdf(customerOrder, creditNote, invoice);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
            try {
                List<Attachment> attachments = attachmentService.addAttachment(new FileInputStream(creditNotePdf),
                        customerOrder.getId(), null,
                        CustomerOrder.class.getSimpleName(),
                        constantService.getAttachmentTypeCreditNote(),
                        "Credit_note_" + creditNote.getId() + "_" + formatter.format(LocalDateTime.now()) + ".pdf",
                        false, "Avoir n°" + creditNote.getId(), null, null, null);

                for (Attachment attachment : attachments)
                    if (attachment.getDescription() != null
                            && attachment.getDescription().contains(creditNote.getId() + "")) {
                        attachment.setInvoice(creditNote);
                        attachmentService.addOrUpdateAttachment(attachment);
                    }
            } catch (FileNotFoundException e) {
                throw new OsirisException(e, "Impossible to read invoice PDF temp file");
            } finally {
                creditNotePdf.delete();
            }
        }

        // Cancel invoice
        invoice.setInvoiceStatus(constantService.getInvoiceStatusCancelled());
        addOrUpdateInvoice(invoice);

        if (customerOrder != null)
            mailHelper.sendCreditNoteToCustomer(customerOrder, false, creditNote);

        return invoice;
    }

    private Invoice cancelInvoiceReceived(Invoice invoice)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        // Refresh invoice
        invoice = getInvoice(invoice.getId());

        if (invoice.getBankTransfert() != null)
            if (invoice.getBankTransfert().getIsAlreadyExported() == false)
                bankTransfertService.cancelBankTransfert(invoice.getBankTransfert());
            else
                throw new OsirisClientMessageException("Virement bancaire déjà exporté, impossible d'annuler !");

        // Unassociate payment
        if (invoice.getPayments() != null && invoice.getPayments().size() > 0)
            for (Payment payment : invoice.getPayments())
                if (!payment.getIsCancelled())
                    throw new OsirisValidationException(
                            "Il reste des paiements sur la facture, impossible d'annuler !");

        // Remove accounting records
        accountingRecordGenerationService.generateAccountingRecordsOnInvoiceReceptionCancellation(invoice);

        // Cancel invoice
        invoice.setInvoiceStatus(constantService.getInvoiceStatusCancelled());
        return addOrUpdateInvoice(invoice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Invoice generateProviderInvoiceCreditNote(Invoice newInvoice, Integer idOriginInvoiceForCreditNote)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {

        Invoice invoice = getInvoice(idOriginInvoiceForCreditNote);
        newInvoice.setIsCreditNote(true);
        newInvoice.setReverseCreditNote(invoice);
        // Create credit note
        Invoice creditNote = addOrUpdateInvoiceFromUser(newInvoice);
        if (newInvoice.getInvoiceItems() != null) {
            creditNote.setInvoiceStatus(constantService.getInvoiceStatusCreditNoteReceived());
            creditNote.setIsCreditNote(true);
        } else {
            throw new OsirisValidationException("Aucune ligne de facturation générée");
        }

        invoice.setCreditNote(creditNote);
        invoice = addOrUpdateInvoice(invoice);
        creditNote.setReverseCreditNote(invoice);

        creditNote = addOrUpdateInvoice(creditNote);

        return creditNote;
    }

    @Override
    public Invoice addOrUpdateInvoice(Invoice invoice) throws OsirisException {
        if (invoice.getIsCreditNote() == null)
            invoice.setIsCreditNote(false);

        invoiceRepository.save(invoice);
        batchService.declareNewBatch(Batch.REINDEX_INVOICE, invoice.getId());
        return invoice;
    }

    @Override
    public List<InvoiceSearchResult> getInvoiceForCustomerOrder(Integer customerOrderId) throws OsirisException {
        return invoiceRepository.findInvoice(Arrays.asList(0), LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), null, null, false, constantService.getInvoiceStatusPayed().getId(),
                0, customerOrderId, Arrays.asList(0), 0, 0,
                constantService.getDocumentTypeBilling().getId());
    }

    @Override
    public List<InvoiceSearchResult> getProviderInvoiceForCustomerOrder(Integer customerOrderId)
            throws OsirisException {
        return invoiceRepository.findInvoice(Arrays.asList(0), LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), null, null, false, constantService.getInvoiceStatusPayed().getId(),
                0, 0, Arrays.asList(0), 0, customerOrderId,
                constantService.getDocumentTypeBilling().getId());
    }

    @Override
    public List<InvoiceSearchResult> searchInvoices(InvoiceSearch invoiceSearch) throws OsirisException {
        ArrayList<Integer> statusId = new ArrayList<Integer>();
        if (invoiceSearch.getInvoiceStatus() != null) {
            for (InvoiceStatus invoiceStatus : invoiceSearch.getInvoiceStatus())
                statusId.add(invoiceStatus.getId());
        }
        if (statusId.size() == 0)
            statusId.add(0);

        if (invoiceSearch.getStartDate() == null)
            invoiceSearch.setStartDate(LocalDateTime.now().minusYears(100));

        if (invoiceSearch.getEndDate() == null)
            invoiceSearch.setEndDate(LocalDateTime.now().plusYears(100));

        if (invoiceSearch.getShowToRecover() == null)
            invoiceSearch.setShowToRecover(false);

        ArrayList<Integer> customerOrderId = new ArrayList<Integer>();
        if (invoiceSearch.getCustomerOrders() != null && invoiceSearch.getCustomerOrders().size() > 0) {
            for (Tiers tiers : invoiceSearch.getCustomerOrders())
                customerOrderId.add(tiers.getId());
        } else {
            customerOrderId.add(0);
        }

        if (invoiceSearch.getCustomerOrderId() == null)
            invoiceSearch.setCustomerOrderId(0);

        if (invoiceSearch.getInvoiceId() == null)
            invoiceSearch.setInvoiceId(0);

        Integer salesEmployeeId = 0;
        if (invoiceSearch.getSalesEmployee() != null)
            salesEmployeeId = invoiceSearch.getSalesEmployee().getId();

        return invoiceRepository.findInvoice(statusId,
                invoiceSearch.getStartDate().withHour(0).withMinute(0),
                invoiceSearch.getEndDate().withHour(23).withMinute(59), invoiceSearch.getMinAmount(),
                invoiceSearch.getMaxAmount(), invoiceSearch.getShowToRecover(),
                constantService.getInvoiceStatusPayed().getId(), invoiceSearch.getInvoiceId(),
                invoiceSearch.getCustomerOrderId(), customerOrderId, salesEmployeeId, 0,
                constantService.getDocumentTypeBilling().getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reindexInvoices() throws OsirisException {
        List<Invoice> invoices = getAllInvoices();
        if (invoices != null)
            for (Invoice invoice : invoices)
                batchService.declareNewBatch(Batch.REINDEX_INVOICE, invoice.getId());
    }

    private boolean hasAtLeastOneInvoiceItemNotNull(Invoice invoice) {
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems())
            if (invoiceItem.getPreTaxPrice().compareTo(BigDecimal.ZERO) > 0) {
                return true;
            }
        return false;
    }

    @Override
    public BigDecimal getRemainingAmountToPayForInvoice(Invoice invoice) throws OsirisException {
        if (invoice != null) {
            BigDecimal total = invoice.getTotalPrice();
            BigDecimal oneHundredValue = new BigDecimal(100);

            if (invoice.getPayments() != null && invoice.getPayments().size() > 0)
                for (Payment payment : invoice.getPayments())
                    if (!payment.getIsCancelled())
                        if (invoice.getProvider() != null)
                            total = total.subtract(payment.getPaymentAmount().abs());
                        else
                            total = total.subtract(payment.getPaymentAmount());

            return total.multiply(oneHundredValue).setScale(0).divide(oneHundredValue);
        }
        return BigDecimal.ZERO;
    }

    private Invoice cloneInvoice(Invoice invoice) {
        Invoice newInvoice = new Invoice();
        newInvoice.setBillingLabel(invoice.getBillingLabel());
        newInvoice.setBillingLabelAddress(invoice.getBillingLabelAddress());
        newInvoice.setBillingLabelCity(invoice.getBillingLabelCity());
        newInvoice.setBillingLabelCountry(invoice.getBillingLabelCountry());
        newInvoice.setBillingLabelIsIndividual(invoice.getBillingLabelIsIndividual());
        newInvoice.setBillingLabelPostalCode(invoice.getBillingLabelPostalCode());
        newInvoice.setBillingLabelIntercommunityVat(invoice.getBillingLabelIntercommunityVat());
        newInvoice.setBillingLabelType(invoice.getBillingLabelType());
        newInvoice.setCedexComplement(invoice.getCedexComplement());
        newInvoice.setCommandNumber(invoice.getCommandNumber());
        // newInvoice.setCompetentAuthority(invoice.getCompetentAuthority());
        // TODO refonte
        newInvoice.setCreatedDate(LocalDateTime.now());
        newInvoice.setDueDate(invoice.getDueDate());
        newInvoice.setIsCommandNumberMandatory(invoice.getIsCommandNumberMandatory());
        newInvoice.setIsResponsableOnBilling(invoice.getIsResponsableOnBilling());
        newInvoice.setManualAccountingDocumentDate(invoice.getManualAccountingDocumentDate());
        newInvoice.setManualAccountingDocumentNumber(invoice.getManualAccountingDocumentNumber());
        newInvoice.setManualPaymentType(invoice.getManualPaymentType());
        newInvoice.setProvider(invoice.getProvider());
        newInvoice.setResponsable(invoice.getResponsable());
        newInvoice.setTotalPrice(invoice.getTotalPrice());
        newInvoice.setIsCreditNote(false);
        return newInvoice;
    }

    @Override
    public CustomerOrder getCustomerOrderByIdInvoice(Integer idInvoice) {
        Invoice invoice = getInvoice(idInvoice);
        if (invoice != null)
            return invoice.getCustomerOrder();
        return null;
    }

    @Override
    @Transactional
    public void sendRemindersForInvoices()
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        List<Invoice> invoices = invoiceRepository.findInvoiceForReminder(constantService.getInvoiceStatusSend());

        if (invoices != null && invoices.size() > 0)
            for (Invoice invoice : invoices) {
                batchService.declareNewBatch(Batch.SEND_REMINDER_FOR_INVOICES, invoice.getId());
            }
    }

    @Override
    @Transactional
    public void sendRemindersForInvoices(BillingLabelType billingLabelType)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        List<Invoice> invoices = invoiceRepository.findInvoiceForCustomReminder(constantService.getInvoiceStatusSend(),
                billingLabelType);

        if (invoices != null && invoices.size() > 0)
            for (Invoice invoice : invoices) {
                batchService.declareNewBatch(Batch.SEND_REMINDER_FOR_INVOICES, invoice.getId());
            }
    }

    @Override
    @Transactional
    public void remindInvoice(Invoice invoice)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        boolean toSend = false;

        // Do not remind on direct debit transfert
        if (invoice != null)
            if (invoice.getManualPaymentType() == null
                    || !invoice.getManualPaymentType().getId()
                            .equals(constantService.getPaymentTypePrelevement().getId())) {
                if (invoice.getFirstReminderDateTime() == null) {
                    if (invoice.getDueDate().isBefore(LocalDate.now().minusDays(8))) {
                        toSend = true;
                        invoice.setFirstReminderDateTime(LocalDateTime.now());
                    }
                } else if (invoice.getSecondReminderDateTime() == null) {
                    if (invoice.getDueDate().isBefore(LocalDate.now().minusDays(8 + 15))
                            && LocalDate.now().minusDays(15)
                                    .isAfter(invoice.getFirstReminderDateTime().toLocalDate())) {
                        toSend = true;
                        invoice.setSecondReminderDateTime(LocalDateTime.now());
                    }
                } else if (invoice.getThirdReminderDateTime() == null) {
                    if (invoice.getDueDate().isBefore(LocalDate.now().minusDays(8 + 15 + 15))
                            && LocalDate.now().minusDays(15)
                                    .isAfter(invoice.getSecondReminderDateTime().toLocalDate())) {
                        toSend = true;
                        invoice.setThirdReminderDateTime(LocalDateTime.now());
                    }
                }

                if (toSend) {
                    mailHelper.sendCustomerOrderFinalisationToCustomer(
                            customerOrderService.getCustomerOrder(invoice.getCustomerOrder().getId()), false, true,
                            invoice.getThirdReminderDateTime() != null);
                    addOrUpdateInvoice(invoice);
                }
            }
    }

    @Override
    public List<Invoice> findByProviderAndManualDocumentNumber(Provider provider,
            String manualDocumentNumber) {
        return invoiceRepository.findByProviderAndManualAccountingDocumentNumberIgnoreCase(provider,
                manualDocumentNumber);
    }

    @Override
    public List<Invoice> findByProviderAndManualDocumentNumberContains(Provider provider,
            String manualDocumentNumber) {
        return invoiceRepository.findByProviderAndManualAccountingDocumentNumberContainingIgnoreCase(
                provider, manualDocumentNumber);
    }

    @Override
    public List<Invoice> searchInvoices(List<InvoiceStatus> invoiceStatus, List<Responsable> responsables) {
        if (invoiceStatus != null && invoiceStatus.size() > 0 && invoiceStatus.size() > 0
                && responsables != null && responsables.size() > 0) {
            return invoiceRepository.searchInvoices(responsables, invoiceStatus);
        }
        return null;
    }
}