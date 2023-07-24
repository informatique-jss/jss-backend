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

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.GeneratePdfDelegate;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.InvoiceSearch;
import com.jss.osiris.modules.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.repository.InvoiceRepository;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.IVat;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.service.BankTransfertService;
import com.jss.osiris.modules.quotation.service.ConfrereService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.PricingHelper;
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
    IndexEntityService indexEntityService;

    @Autowired
    AccountingRecordService accountingRecordService;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Invoice cancelInvoice(Invoice invoice)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        invoice = getInvoice(invoice.getId());
        if (invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId()))
            return cancelInvoiceEmitted(invoice, invoice.getCustomerOrder());
        if (invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusReceived().getId())
                || invoice.getIsInvoiceFromProvider()
                        && invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusPayed().getId()))
            return cancelInvoiceReceived(invoice);
        return invoice;
    }

    @Override
    public Invoice cancelInvoiceEmitted(Invoice invoice, CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        // Remove accounting records and unletter invoice
        // TODO

        // Refresh invoice
        invoice = getInvoice(invoice.getId());

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
        creditNote = addOrUpdateInvoice(creditNote);
        if (invoice.getInvoiceItems() != null) {
            ArrayList<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                InvoiceItem newInvoiceItem = invoiceItemService.cloneInvoiceItem(invoiceItem);
                newInvoiceItem.setInvoice(creditNote);
                invoiceItemService.addOrUpdateInvoiceItem(newInvoiceItem);
                invoiceItems.add(newInvoiceItem);
            }
            creditNote.setInvoiceItems(invoiceItems);
            creditNote.setInvoiceStatus(constantService.getInvoiceStatusCreditNoteEmited());
            creditNote.setIsCreditNote(true);
        }

        invoice.setCreditNote(creditNote);
        invoice = addOrUpdateInvoice(invoice);
        creditNote = addOrUpdateInvoice(creditNote);

        accountingRecordService.letterCreditNoteAndInvoice(invoice, creditNote);

        if (customerOrder != null) {
            // Generate PDF and attached it to customer order
            File creditNotePdf = generatePdfDelegate.generateInvoicePdf(customerOrder, creditNote, invoice);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
            try {
                attachmentService.addAttachment(new FileInputStream(creditNotePdf), customerOrder.getId(),
                        CustomerOrder.class.getSimpleName(),
                        constantService.getAttachmentTypeCreditNote(),
                        "Credit_note_" + creditNote.getId() + "_" + formatter.format(LocalDateTime.now()) + ".pdf",
                        false, "Avoir n°" + creditNote.getId());
            } catch (FileNotFoundException e) {
                throw new OsirisException(e, "Impossible to read invoice PDF temp file");
            } finally {
                creditNotePdf.delete();
            }
        }

        // Cancel invoice
        invoice.setInvoiceStatus(constantService.getInvoiceStatusCancelled());
        addOrUpdateInvoice(invoice);

        if (customerOrder != null) {
            mailHelper.sendCreditNoteToCustomer(customerOrder, false, creditNote, invoice);
        }

        return invoice;
    }

    private Invoice cancelInvoiceReceived(Invoice invoice)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        // Unletter
        accountingRecordService.unletterInvoiceReceived(invoice);

        // Remove accounting records
        // TODO

        // Refresh invoice
        invoice = getInvoice(invoice.getId());

        if (invoice.getBankTransfert() != null && invoice.getBankTransfert().getIsAlreadyExported() == false)
            bankTransfertService.cancelBankTransfert(invoice.getBankTransfert());
        else
            throw new OsirisValidationException("Virement bancaire déjà exporté, impossible d'annuler !");

        // Unassociate payment
        if (invoice.getPayments() != null && invoice.getPayments().size() > 0)
            throw new OsirisValidationException("Il reste des paiements sur la facture, impossible d'annuler !");

        // Cancel invoice
        invoice.setInvoiceStatus(constantService.getInvoiceStatusCancelled());
        return addOrUpdateInvoice(invoice);
    }

    @Override
    public Invoice generateInvoiceCreditNote(Invoice newInvoice, Integer idOriginInvoiceForCreditNote)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        newInvoice.setIsInvoiceFromProvider(false);
        newInvoice.setIsProviderCreditNote(true);
        // Create credit note
        Invoice creditNote = addOrUpdateInvoiceFromUser(newInvoice);
        if (newInvoice.getInvoiceItems() != null) {
            creditNote.setInvoiceStatus(constantService.getInvoiceStatusCreditNoteReceived());
            creditNote.setIsProviderCreditNote(true);
        } else {
            throw new OsirisValidationException("Aucune ligne de facturation générée");
        }

        Invoice invoice = getInvoice(idOriginInvoiceForCreditNote);
        invoice.setCreditNote(creditNote);
        invoice = addOrUpdateInvoice(invoice);
        creditNote.setReverseCreditNote(invoice);

        creditNote = addOrUpdateInvoice(creditNote);

        return creditNote;
    }

    @Override
    public Invoice addOrUpdateInvoice(Invoice invoice) {
        if (invoice.getIsCreditNote() == null)
            invoice.setIsCreditNote(false);
        if (invoice.getIsInvoiceFromProvider() == null)
            invoice.setIsInvoiceFromProvider(false);
        if (invoice.getIsProviderCreditNote() == null)
            invoice.setIsProviderCreditNote(false);

        invoiceRepository.save(invoice);
        indexEntityService.indexEntity(invoice, invoice.getId());
        return invoice;
    }

    @Override
    public Invoice createInvoice(CustomerOrder customerOrder, ITiers orderingCustomer)
            throws OsirisException, OsirisClientMessageException {
        Invoice invoice = new Invoice();
        invoice.setCreatedDate(LocalDateTime.now());

        if (orderingCustomer instanceof Tiers)
            invoice.setTiers((Tiers) orderingCustomer);

        if (orderingCustomer instanceof Responsable)
            invoice.setResponsable((Responsable) orderingCustomer);

        if (orderingCustomer instanceof Confrere)
            invoice.setConfrere((Confrere) orderingCustomer);

        ITiers masterOrderingCustomer = orderingCustomer;
        if (orderingCustomer instanceof Responsable)
            masterOrderingCustomer = ((Responsable) orderingCustomer).getTiers();

        Document dunningDocument = documentService.getDocumentByDocumentType(masterOrderingCustomer.getDocuments(),
                constantService.getDocumentTypeDunning());

        if (dunningDocument == null)
            throw new OsirisClientMessageException("Merci de remplir la partie réglement pour le donneur d'ordre");

        if (dunningDocument.getPaymentDeadlineType() == null)
            throw new OsirisClientMessageException("Merci de remplir la partie réglement pour le donneur d'ordre");

        Integer nbrOfDayFromDueDate = 30;
        if (dunningDocument.getPaymentDeadlineType() != null)
            nbrOfDayFromDueDate = dunningDocument.getPaymentDeadlineType().getNumberOfDay();
        invoice.setDueDate(LocalDate.now().plusDays(nbrOfDayFromDueDate));

        Document billingDocument = documentService.getBillingDocument(customerOrder.getDocuments());

        if (billingDocument == null)
            throw new OsirisException(null, "Billing document not found for ordering customer provided");

        invoiceHelper.setInvoiceLabel(invoice, billingDocument, customerOrder, orderingCustomer);
        invoice.setIsCreditNote(false);
        invoice.setIsProviderCreditNote(false);

        InvoiceStatus statusSent = constantService.getInvoiceStatusSend();

        invoice.setInvoiceStatus(statusSent);
        return this.addOrUpdateInvoice(invoice);
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
    public void reindexInvoices() {
        List<Invoice> invoices = getAllInvoices();
        if (invoices != null)
            for (Invoice invoice : invoices)
                indexEntityService.indexEntity(invoice, invoice.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Invoice addOrUpdateInvoiceFromUser(Invoice invoice)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        if (!hasAtLeastOneInvoiceItemNotNull(invoice))
            throw new OsirisException(null, "No invoice item found on manual invoice");

        IVat vatTiers = null;
        if (invoice.getTiers() != null)
            vatTiers = invoice.getTiers();
        if (invoice.getResponsable() != null)
            vatTiers = invoice.getResponsable().getTiers();
        if (invoice.getProvider() != null)
            vatTiers = invoice.getProvider();
        if (invoice.getConfrere() != null)
            vatTiers = invoice.getConfrere();
        if (invoice.getCompetentAuthority() != null)
            vatTiers = invoice.getCompetentAuthority();

        if (vatTiers == null)
            throw new OsirisValidationException("vatTiers");

        if (invoice.getId() != null)
            throw new OsirisClientMessageException("Impossible d'éditer une facture émise.");

        ITiers masterOrderingCustomer = null;
        Integer nbrOfDayFromDueDate = 30;

        if (invoice.getResponsable() != null)
            masterOrderingCustomer = invoice.getResponsable().getTiers();
        if (invoice.getConfrere() != null)
            masterOrderingCustomer = invoice.getConfrere();
        if (invoice.getTiers() != null)
            masterOrderingCustomer = invoice.getTiers();

        if (masterOrderingCustomer != null) {
            Document dunningDocument = documentService.getDocumentByDocumentType(
                    masterOrderingCustomer.getDocuments(),
                    constantService.getDocumentTypeDunning());

            if (dunningDocument.getPaymentDeadlineType() != null)
                nbrOfDayFromDueDate = dunningDocument.getPaymentDeadlineType().getNumberOfDay();
        }

        if (invoice.getDueDate() == null)
            invoice.setDueDate(LocalDate.now().plusDays(nbrOfDayFromDueDate));

        invoice.setCreatedDate(LocalDateTime.now());

        // TODO : if customerOrder, grab provider invoices and autogenerate invoice item
        // reinvoiced
        if (invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0)
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                Vat invoiceItemVat = invoiceItem.getVat();

                if (invoiceItemVat == null || invoiceItemVat.getRate() > 0) {
                    Vat salesVat = null;
                    Vat purschaseVat = null;
                    if (invoice.getCustomerOrder() != null)
                        salesVat = vatService.getGeographicalApplicableVatForSales(invoice.getCustomerOrder(),
                                invoiceItemVat);
                    if (invoice.getCustomerOrderForInboundInvoice() != null)
                        salesVat = vatService.getGeographicalApplicableVatForSales(
                                invoice.getCustomerOrderForInboundInvoice(), invoiceItemVat);
                    if (invoice.getIsInvoiceFromProvider() || invoice.getIsProviderCreditNote())
                        purschaseVat = vatService.getGeographicalApplicableVatForPurshases(vatTiers, invoiceItemVat);

                    if (invoiceItemVat == null) {
                        if (salesVat != null)
                            invoiceItemVat = salesVat;
                        else if (purschaseVat != null)
                            invoiceItemVat = purschaseVat;
                        else
                            invoiceItemVat = invoice.getIsInvoiceFromProvider() ? constantService.getVatDeductible()
                                    : constantService.getVatTwenty();
                    }

                    if (salesVat != null && salesVat.getRate() < invoiceItemVat.getRate())
                        invoiceItemVat = salesVat;

                    if (purschaseVat != null && purschaseVat.getRate() < invoiceItemVat.getRate())
                        invoiceItemVat = purschaseVat;
                }

                invoiceItem.setVat(invoiceItemVat);

                if (invoiceItem.getBillingItem().getBillingType().getIsNonTaxable() || invoiceItemVat == null
                        || invoiceItemVat.getRate() == 0)
                    invoiceItem.setVatPrice(0f);
                else
                    invoiceItem.setVatPrice(invoiceItem.getVat().getRate() * invoiceItem.getPreTaxPrice() / 100f);
            }

        // Defined billing label
        if (!invoice.getIsInvoiceFromProvider() && !invoice.getIsProviderCreditNote()
                && !constantService.getBillingLabelTypeOther().getId().equals(invoice.getBillingLabelType().getId())) {

            ITiers customerOrder = invoice.getTiers();
            if (invoice.getResponsable() != null)
                customerOrder = invoice.getResponsable();
            if (invoice.getConfrere() != null)
                customerOrder = invoice.getConfrere();
            Document billingDocument = documentService.getBillingDocument(customerOrder.getDocuments());

            invoiceHelper.setInvoiceLabel(invoice, billingDocument, null, customerOrder);
        }

        if (invoice.getIsInvoiceFromProvider())
            invoice.setInvoiceStatus(constantService.getInvoiceStatusReceived());
        else if (invoice.getIsProviderCreditNote())
            invoice.setInvoiceStatus(constantService.getInvoiceStatusCreditNoteReceived());
        else
            invoice.setInvoiceStatus(constantService.getInvoiceStatusSend());

        // Save before to have an ID on invoice
        addOrUpdateInvoice(invoice);

        // Associate invoice to invoice item
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            invoiceItem.setInvoice(invoice);
        }

        invoiceHelper.setPriceTotal(invoice);

        if (invoice.getIsInvoiceFromProvider())
            accountingRecordService.generateAccountingRecordsOnInvoiceReception(invoice);
        else if (invoice.getIsProviderCreditNote())
            accountingRecordService.generateAccountingRecordsOnCreditNoteReception(invoice);
        else
            accountingRecordService.generateAccountingRecordsOnInvoiceEmission(invoice);

        // Generate bank transfert if invoice from Provider
        if (invoice.getIsInvoiceFromProvider()) {
            if (invoice.getManualPaymentType().getId().equals(constantService.getPaymentTypeVirement().getId())) {
                invoice.setBankTransfert(bankTransfertService.generateBankTransfertForManualInvoice(invoice));
            }
            if (invoice.getManualPaymentType().getId().equals(constantService.getPaymentTypeAccount().getId())) {
                paymentService.generateNewAccountPayment(invoice.getTotalPrice(),
                        vatTiers.getAccountingAccountProvider());
            }
        }

        addOrUpdateInvoice(invoice);
        return invoice;
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
        invoice = getInvoice(invoice.getId());
        if (invoice != null) {
            Float total = invoice.getTotalPrice();

            if (invoice.getPayments() != null && invoice.getPayments().size() > 0)
                for (Payment payment : invoice.getPayments())
                    total -= Math.abs(payment.getPaymentAmount());

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
                boolean toSend = false;

                // Do not remind on direct debit transfert
                if (invoice.getManualPaymentType() == null
                        || !invoice.getManualPaymentType().getId()
                                .equals(constantService.getPaymentTypePrelevement().getId())) {
                    if (invoice.getFirstReminderDateTime() == null
                            && invoice.getDueDate().isBefore(LocalDate.now().minusDays(8))) {
                        toSend = true;
                        invoice.setFirstReminderDateTime(LocalDateTime.now());

                        ITiers customerOrderToSetProvision = invoiceHelper.getCustomerOrder(invoice);
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
    }
}