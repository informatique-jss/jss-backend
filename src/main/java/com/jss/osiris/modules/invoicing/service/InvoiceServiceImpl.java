package com.jss.osiris.modules.invoicing.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import com.jss.osiris.modules.accounting.service.AccountingRecordGenerationService;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.InvoiceSearch;
import com.jss.osiris.modules.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.repository.InvoiceRepository;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.IGenericTiers;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.service.BankTransfertService;
import com.jss.osiris.modules.quotation.service.ConfrereService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.DirectDebitTransfertService;
import com.jss.osiris.modules.quotation.service.PricingHelper;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.service.TiersService;

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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Invoice addOrUpdateInvoiceFromUser(Invoice invoice)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        if (!hasAtLeastOneInvoiceItemNotNull(invoice))
            throw new OsirisException(null, "No invoice item found on invoice");

        if (invoice.getId() != null)
            throw new OsirisClientMessageException("Impossible de modifier une facture");

        IGenericTiers invoiceTiers = invoiceHelper.getCustomerOrder(invoice);

        // Define booleans
        if (invoice.getIsCreditNote() == null)
            invoice.setIsCreditNote(false);
        if (invoice.getIsInvoiceFromProvider() == null)
            invoice.setIsInvoiceFromProvider(false);
        if (invoice.getIsProviderCreditNote() == null)
            invoice.setIsProviderCreditNote(false);

        // Define due date
        if (invoice.getDueDate() == null)
            invoice.setDueDate(getDueDateForInvoice(invoiceTiers, invoice));

        invoice.setCreatedDate(LocalDateTime.now());

        // Defined billing label
        // If it's a credit note, no need, label is taken from invoice clone
        if (!invoice.getIsInvoiceFromProvider() && !invoice.getIsProviderCreditNote() && !invoice.getIsCreditNote()
                && (invoice.getBillingLabelType() == null || !constantService.getBillingLabelTypeOther().getId()
                        .equals(invoice.getBillingLabelType().getId()))) {
            Document billingDocument = null;
            ITiers customerOrder = invoice.getTiers();
            if (invoice.getResponsable() != null)
                customerOrder = invoice.getResponsable();
            if (invoice.getConfrere() != null)
                customerOrder = invoice.getConfrere();

            if (invoice.getCustomerOrder() != null)
                billingDocument = documentService.getBillingDocument(invoice.getCustomerOrder().getDocuments());
            else
                billingDocument = documentService.getBillingDocument(customerOrder.getDocuments());

            if (billingDocument == null)
                throw new OsirisException(null, "Billing document not found for ordering customer provided");

            invoice.setBillingLabelType(billingDocument.getBillingLabelType());
            invoiceHelper.setInvoiceLabel(invoice, billingDocument, invoice.getCustomerOrder(), customerOrder);
        }

        // Define status
        if (invoice.getIsInvoiceFromProvider())
            invoice.setInvoiceStatus(constantService.getInvoiceStatusReceived());
        else if (invoice.getIsProviderCreditNote())
            invoice.setInvoiceStatus(constantService.getInvoiceStatusCreditNoteReceived());
        else if (invoice.getInvoiceStatus() == null)
            invoice.setInvoiceStatus(constantService.getInvoiceStatusSend());

        // Save before to have an ID on invoice
        addOrUpdateInvoice(invoice);

        // Associate invoice to invoice item
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            // If we got an AC with direct charge, force no VAT
            if (invoice.getIsInvoiceFromProvider() && invoice.getCompetentAuthority() != null
                    && invoice.getCompetentAuthority().getCompetentAuthorityType().getIsDirectCharge())
                invoiceItem.setVat(constantService.getVatZero());

            vatService.completeVatOnInvoiceItem(invoiceItem, invoice);
            invoiceItem.setInvoice(invoice);
            invoiceItemService.addOrUpdateInvoiceItem(invoiceItem);
        }

        invoiceHelper.setPriceTotal(invoice);

        // Generate accounting records
        if (invoice.getIsInvoiceFromProvider())
            accountingRecordGenerationService.generateAccountingRecordsOnInvoiceReception(invoice);
        else if (invoice.getIsProviderCreditNote())
            accountingRecordGenerationService.generateAccountingRecordsOnCreditNoteReception(invoice,
                    invoice.getReverseCreditNote());
        else if (invoice.getIsCreditNote())
            accountingRecordGenerationService
                    .generateAccountingRecordsOnInvoiceEmissionCancellation(invoice.getReverseCreditNote(), invoice);
        else
            accountingRecordGenerationService.generateAccountingRecordsOnInvoiceEmission(invoice);

        // Define payment type
        if (invoice.getManualPaymentType() == null) {
            PaymentType paymentType = null;
            if (invoice.getIsInvoiceFromProvider()) {
                if (invoice.getProvider() != null)
                    paymentType = invoice.getProvider().getPaymentType();
                if (invoice.getConfrere() != null)
                    paymentType = invoice.getConfrere().getPaymentType();
                if (invoice.getCompetentAuthority() != null)
                    paymentType = invoice.getCompetentAuthority().getDefaultPaymentType();
            } else {
                paymentType = constantService.getPaymentTypeVirement();
                if (invoice.getResponsable() != null && invoice.getResponsable().getTiers().getPaymentType() != null)
                    paymentType = invoice.getResponsable().getTiers().getPaymentType();
                if (invoice.getTiers() != null && invoice.getTiers().getPaymentType() != null)
                    paymentType = invoice.getTiers().getPaymentType();
                if (invoice.getConfrere() != null && invoice.getConfrere().getPaymentType() != null)
                    paymentType = invoice.getConfrere().getPaymentType();
            }
            invoice.setManualPaymentType(paymentType);
        }

        // Handle provider and customer payment
        if (invoice.getIsInvoiceFromProvider()) {
            if (invoice.getManualPaymentType().getId().equals(constantService.getPaymentTypeVirement().getId())) {
                invoice.setBankTransfert(bankTransfertService.generateBankTransfertForManualInvoice(invoice));
            }

            if (invoice.getManualPaymentType().getId().equals(constantService.getPaymentTypeAccount().getId())) {
                Payment payment = paymentService.generateNewAccountPayment(-invoice.getTotalPrice(),
                        invoiceTiers.getAccountingAccountDeposit(), invoiceTiers.getAccountingAccountProvider(),
                        "Paiement pour la facture " + invoice.getId() + " / Fournisseur : "
                                + (invoice.getProvider() != null ? invoice.getProvider().getLabel()
                                        : invoice.getCompetentAuthority().getLabel()));
                accountingRecordGenerationService.generateAccountingRecordOnOutgoingPaymentCreation(payment, false);
                paymentService.manualMatchPaymentInvoicesAndCustomerOrders(payment, Arrays.asList(invoice), null, null,
                        null, null, null, null);
            }
        } else {
            if (invoice.getManualPaymentType().getId().equals(constantService.getPaymentTypePrelevement().getId())
                    && !invoice.getIsCreditNote()) {
                invoice.setDirectDebitTransfert(
                        directDebitTransfertService.generateDirectDebitTransfertForOutboundInvoice(invoice));
            }
        }

        addOrUpdateInvoice(invoice);

        // Associate attachment for azure invoice
        if (invoice.getAzureInvoice() != null) {
            Attachment attachment = attachmentService
                    .getAttachment(invoice.getAzureInvoice().getAttachments().get(0).getId());
            attachment.setInvoice(invoice);
            attachmentService.addOrUpdateAttachment(attachment);
        }
        return invoice;
    }

    private LocalDate getDueDateForInvoice(IGenericTiers customerOrder, Invoice invoice)
            throws OsirisException, OsirisClientMessageException {
        Integer nbrOfDayFromDueDate = 30;
        ITiers masterOrderingCustomer = null;

        if (customerOrder instanceof ITiers)
            masterOrderingCustomer = (ITiers) customerOrder;

        if (customerOrder instanceof Responsable)
            masterOrderingCustomer = ((Responsable) customerOrder).getTiers();

        if (masterOrderingCustomer != null) {
            Document dunningDocument = documentService.getDocumentByDocumentType(masterOrderingCustomer.getDocuments(),
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
                || !invoice.getIsCreditNote() && !invoice.getIsInvoiceFromProvider()
                        && !invoice.getIsProviderCreditNote())
            return cancelInvoiceEmitted(invoice, invoice.getCustomerOrder());
        if (invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusReceived().getId())
                || invoice.getInvoiceStatus().getId()
                        .equals(constantService.getInvoiceStatusCreditNoteReceived().getId())
                || invoice.getIsInvoiceFromProvider()
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
                            Tiers tiersInvoice = invoice.getResponsable() != null ? invoice.getResponsable().getTiers()
                                    : invoice.getTiers();
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
                        customerOrder.getId(),
                        CustomerOrder.class.getSimpleName(),
                        constantService.getAttachmentTypeCreditNote(),
                        "Credit_note_" + creditNote.getId() + "_" + formatter.format(LocalDateTime.now()) + ".pdf",
                        false, "Avoir n°" + creditNote.getId(), null, null);

                for (Attachment attachment : attachments)
                    if (attachment.getDescription().contains(creditNote.getId() + "")) {
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
            mailHelper.sendCreditNoteToCustomer(customerOrder, false, creditNote, invoice);

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
        newInvoice.setIsInvoiceFromProvider(false);
        newInvoice.setIsProviderCreditNote(true);
        newInvoice.setReverseCreditNote(invoice);
        // Create credit note
        Invoice creditNote = addOrUpdateInvoiceFromUser(newInvoice);
        if (newInvoice.getInvoiceItems() != null) {
            creditNote.setInvoiceStatus(constantService.getInvoiceStatusCreditNoteReceived());
            creditNote.setIsProviderCreditNote(true);
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
        if (invoice.getIsInvoiceFromProvider() == null)
            invoice.setIsInvoiceFromProvider(false);
        if (invoice.getIsProviderCreditNote() == null)
            invoice.setIsProviderCreditNote(false);

        invoiceRepository.save(invoice);
        batchService.declareNewBatch(Batch.REINDEX_INVOICE, invoice.getId());
        return invoice;
    }

    @Override
    public List<InvoiceSearchResult> getInvoiceForCustomerOrder(Integer customerOrderId) throws OsirisException {
        return invoiceRepository.findInvoice(Arrays.asList(0), LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), null, null, false, constantService.getInvoiceStatusPayed().getId(),
                0, customerOrderId, Arrays.asList(0), 0, constantService.getDocumentTypeBilling().getId());
    }

    @Override
    public List<InvoiceSearchResult> getProviderInvoiceForCustomerOrder(Integer customerOrderId)
            throws OsirisException {
        return invoiceRepository.findInvoice(Arrays.asList(0), LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), null, null, false, constantService.getInvoiceStatusPayed().getId(),
                0, 0, Arrays.asList(0), customerOrderId, constantService.getDocumentTypeBilling().getId());
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
            for (ITiers tiers : invoiceSearch.getCustomerOrders())
                customerOrderId.add(tiers.getId());
        } else {
            customerOrderId.add(0);
        }

        if (invoiceSearch.getCustomerOrderId() == null)
            invoiceSearch.setCustomerOrderId(0);

        if (invoiceSearch.getInvoiceId() == null)
            invoiceSearch.setInvoiceId(0);

        return invoiceRepository.findInvoice(statusId,
                invoiceSearch.getStartDate().withHour(0).withMinute(0),
                invoiceSearch.getEndDate().withHour(23).withMinute(59), invoiceSearch.getMinAmount(),
                invoiceSearch.getMaxAmount(), invoiceSearch.getShowToRecover(),
                constantService.getInvoiceStatusPayed().getId(), invoiceSearch.getInvoiceId(),
                invoiceSearch.getCustomerOrderId(), customerOrderId, 0,
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
            if (invoiceItem.getPreTaxPrice() > 0) {
                return true;
            }
        return false;
    }

    @Override
    public Float getRemainingAmountToPayForInvoice(Invoice invoice) throws OsirisException {
        if (invoice != null) {
            Float total = invoice.getTotalPrice();

            if (invoice.getPayments() != null && invoice.getPayments().size() > 0)
                for (Payment payment : invoice.getPayments())
                    if (!payment.getIsCancelled())
                        if (invoice.getIsInvoiceFromProvider())
                            total -= Math.abs(payment.getPaymentAmount());
                        else
                            total -= payment.getPaymentAmount();

            return Math.round(total * 100f) / 100f;
        }
        return 0f;
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
        newInvoice.setCompetentAuthority(invoice.getCompetentAuthority());
        newInvoice.setConfrere(invoice.getConfrere());
        newInvoice.setCreatedDate(LocalDateTime.now());
        newInvoice.setDueDate(invoice.getDueDate());
        newInvoice.setIsCommandNumberMandatory(invoice.getIsCommandNumberMandatory());
        newInvoice.setIsResponsableOnBilling(invoice.getIsResponsableOnBilling());
        newInvoice.setManualAccountingDocumentDate(invoice.getManualAccountingDocumentDate());
        newInvoice.setManualAccountingDocumentNumber(invoice.getManualAccountingDocumentNumber());
        newInvoice.setManualPaymentType(invoice.getManualPaymentType());
        newInvoice.setProvider(invoice.getProvider());
        newInvoice.setResponsable(invoice.getResponsable());
        newInvoice.setTiers(invoice.getTiers());
        newInvoice.setTotalPrice(invoice.getTotalPrice());
        newInvoice.setIsCreditNote(false);
        newInvoice.setIsProviderCreditNote(false);
        newInvoice.setIsInvoiceFromProvider(invoice.getIsInvoiceFromProvider());
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
    public void sendRemindersForInvoices(LocalDate startDate, LocalDate endDate, BillingLabelType billingLabelType)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        List<Invoice> invoices = invoiceRepository.findInvoiceForCustomReminder(constantService.getInvoiceStatusSend(),
                startDate, endDate, billingLabelType);

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
                if (invoice.getFirstReminderDateTime() == null
                        && invoice.getDueDate().isBefore(LocalDate.now().minusDays(8))) {
                    toSend = true;
                    invoice.setFirstReminderDateTime(LocalDateTime.now());

                    IGenericTiers customerOrderToSetProvision = invoiceHelper.getCustomerOrder(invoice);
                    if (customerOrderToSetProvision instanceof Tiers)
                        notificationService.notifyTiersDepositMandatory((Tiers) customerOrderToSetProvision, null,
                                invoice);
                    else if (customerOrderToSetProvision instanceof Responsable)
                        notificationService.notifyTiersDepositMandatory(null,
                                (Responsable) customerOrderToSetProvision,
                                invoice);
                } else if (invoice.getSecondReminderDateTime() == null
                        && invoice.getDueDate().isBefore(LocalDate.now().minusDays(8 + 15))) {
                    toSend = true;
                    invoice.setSecondReminderDateTime(LocalDateTime.now());
                } else if (invoice.getThirdReminderDateTime() == null
                        && invoice.getDueDate().isBefore(LocalDate.now().minusDays(8 + 15 + 15))) {
                    toSend = true;
                    invoice.setThirdReminderDateTime(LocalDateTime.now());
                    notificationService.notifyInvoiceToReminder(invoice);
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
    public List<Invoice> findByCompetentAuthorityAndManualDocumentNumber(CompetentAuthority competentAuthority,
            String manualDocumentNumber) {
        return invoiceRepository.findByCompetentAuthorityAndManualAccountingDocumentNumber(competentAuthority,
                manualDocumentNumber);
    }

    @Override
    public List<Invoice> findByCompetentAuthorityAndManualDocumentNumberContains(CompetentAuthority competentAuthority,
            String manualDocumentNumber) {
        return invoiceRepository.findByCompetentAuthorityAndManualAccountingDocumentNumberContainingIgnoreCase(
                competentAuthority, manualDocumentNumber);
    }
}