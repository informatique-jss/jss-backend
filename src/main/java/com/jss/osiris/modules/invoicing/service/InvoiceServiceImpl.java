package com.jss.osiris.modules.invoicing.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.InvoiceSearch;
import com.jss.osiris.modules.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.repository.InvoiceRepository;
import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.service.BankTransfertService;
import com.jss.osiris.modules.quotation.service.ConfrereService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.DebourService;
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
    DebourService debourService;

    @Autowired
    PricingHelper pricingHelper;

    @Autowired
    BillingItemService billingItemService;

    @Autowired
    PaymentService paymentService;

    @Override
    public List<Invoice> getAllInvoices() {
        return IterableUtils.toList(invoiceRepository.findAll());
    }

    @Override
    public Invoice getInvoice(Integer id) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (invoice.isPresent())
            return invoice.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Invoice cancelInvoiceFromUser(Invoice invoice)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        return cancelInvoice(invoice, null);
    }

    @Override
    public Invoice cancelInvoice(Invoice invoice, CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        // Unletter
        unletterInvoice(invoice);

        // Remove accounting records
        Integer operationIdCounterPart = ThreadLocalRandom.current().nextInt(1, 1000000000);
        if (invoice.getAccountingRecords() != null)
            for (AccountingRecord accountingRecord : invoice.getAccountingRecords()) {
                accountingRecordService.unassociateCustomerOrderPayementAndDeposit(accountingRecord);
                if (accountingRecord.getIsCounterPart() == null || !accountingRecord.getIsCounterPart())
                    accountingRecordService.generateCounterPart(accountingRecord, operationIdCounterPart);
            }

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

        if (customerOrder != null) {
            // Generate PDF and attached it to customer order
            File creditNotePdf = mailHelper.generateInvoicePdf(customerOrder, creditNote, invoice);
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

    @Override
    public Invoice addOrUpdateInvoice(Invoice invoice) {
        invoiceRepository.save(invoice);
        indexEntityService.indexEntity(invoice, invoice.getId());
        return invoice;
    }

    @Override
    public Invoice createInvoice(CustomerOrder customerOrder, ITiers orderingCustomer) throws OsirisException {
        Invoice invoice = new Invoice();
        invoice.setCreatedDate(LocalDateTime.now());
        Document billingDocument = documentService.getBillingDocument(customerOrder.getDocuments());

        if (billingDocument == null)
            throw new OsirisException(null, "Billing document not found for ordering customer provided");

        if (orderingCustomer instanceof Tiers)
            invoice.setTiers((Tiers) orderingCustomer);

        if (orderingCustomer instanceof Responsable)
            invoice.setResponsable((Responsable) orderingCustomer);

        if (orderingCustomer instanceof Confrere)
            invoice.setConfrere((Confrere) orderingCustomer);

        Integer nbrOfDayFromDueDate = 30;
        if (billingDocument.getPaymentDeadlineType() != null)
            nbrOfDayFromDueDate = billingDocument.getPaymentDeadlineType().getNumberOfDay();
        invoice.setDueDate(LocalDate.now().plusDays(nbrOfDayFromDueDate));

        invoiceHelper.setInvoiceLabel(invoice, billingDocument, customerOrder, orderingCustomer);
        invoice.setIsCreditNote(false);

        InvoiceStatus statusSent = constantService.getInvoiceStatusSend();

        invoice.setInvoiceStatus(statusSent);
        return this.addOrUpdateInvoice(invoice);
    }

    @Override
    public List<InvoiceSearchResult> getInvoiceForCustomerOrder(Integer customerOrderId) throws OsirisException {
        return invoiceRepository.findInvoice(Arrays.asList(0), LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), null, null, false, constantService.getInvoiceStatusPayed().getId(),
                0, customerOrderId, Arrays.asList(0), 0);
    }

    @Override
    public List<InvoiceSearchResult> getProviderInvoiceForCustomerOrder(Integer customerOrderId)
            throws OsirisException {
        return invoiceRepository.findInvoice(Arrays.asList(0), LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), null, null, false, constantService.getInvoiceStatusPayed().getId(),
                0, 0, Arrays.asList(0), customerOrderId);
    }

    @Override
    public LocalDate getFirstBillingDateForTiers(Tiers tiers) {
        return invoiceRepository.findFirstBillingDateForTiers(tiers);
    }

    @Override
    public LocalDate getFirstBillingDateForResponsable(Responsable responsable) {
        return invoiceRepository.findFirstBillingDateForResponsable(responsable);
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
                invoiceSearch.getCustomerOrderId(), customerOrderId, 0);
    }

    @Override
    public void reindexInvoices() {
        List<Invoice> invoices = getAllInvoices();
        if (invoices != null)
            for (Invoice invoice : invoices)
                indexEntityService.indexEntity(invoice, invoice.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Invoice addOrUpdateInvoiceFromUser(Invoice invoice) throws OsirisException, OsirisClientMessageException {
        if (!hasAtLeastOneInvoiceItemNotNull(invoice)
                && (invoice.getCompetentAuthority() == null || invoice.getCustomerOrderForInboundInvoice() == null))
            throw new OsirisException(null, "No invoice item found on manual invoice");

        boolean isNewInvoice = invoice.getId() == null;
        HashMap<Integer, Debour> debourPayments = new HashMap<Integer, Debour>();

        if (isNewInvoice) {
            invoice.setCreatedDate(LocalDateTime.now());
            invoice.setIsCreditNote(false);

            if (invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0)
                for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                    if (invoiceItem.getBillingItem().getBillingType().getIsNonTaxable())
                        invoiceItem.setVatPrice(0f);
                    else
                        invoiceItem.setVatPrice(invoiceItem.getVat().getRate() * invoiceItem.getPreTaxPrice() / 100f);
                }
            else if (invoice.getCompetentAuthority() != null && invoice.getCustomerOrderForInboundInvoice() != null) {
                // Handle invoice item generation based on debours
                if (invoice.getCustomerOrderForInboundInvoice().getAssoAffaireOrders() != null) {
                    invoice.setInvoiceItems(new ArrayList<InvoiceItem>());
                    for (AssoAffaireOrder asso : invoice.getCustomerOrderForInboundInvoice().getAssoAffaireOrders())
                        if (asso.getProvisions() != null)
                            for (Provision provision : asso.getProvisions())
                                if (provision.getDebours() != null)
                                    for (Debour debour : provision.getDebours()) {
                                        if (debour.getBankTransfert() == null && debour.getPaymentType().getId()
                                                .equals(constantService.getPaymentTypeVirement().getId())) {
                                            debour = debourService.getDebour(debour.getId());
                                            debour.setBankTransfert(
                                                    bankTransfertService.generateBankTransfertForDebour(debour,
                                                            debour.getProvision().getAssoAffaireOrder(),
                                                            invoice.getCustomerOrderForInboundInvoice()));
                                            debour = debourService.addOrUpdateDebour(debour);
                                        }

                                        if (debour.getNonTaxableAmount() != null && debour.getInvoiceItem() == null) {

                                            if (debour.getPaymentType().getId()
                                                    .equals(constantService.getPaymentTypeAccount().getId()))
                                                accountingRecordService
                                                        .generateBankAccountingRecordsForOutboundDebourPayment(debour,
                                                                invoice.getCustomerOrderForInboundInvoice());

                                            Debour nonTaxableDebour = null;
                                            if (debour.getPayment() != null)
                                                debourPayments.put(debour.getPayment().getId(), debour);

                                            if (debour.getNonTaxableAmount() > 0
                                                    && debour.getNonTaxableAmount() < debour.getDebourAmount()) {
                                                nonTaxableDebour = new Debour();
                                                nonTaxableDebour
                                                        .setBillingType(
                                                                constantService.getBillingTypeDeboursNonTaxable());
                                                nonTaxableDebour.setCompetentAuthority(debour.getCompetentAuthority());
                                                nonTaxableDebour.setDebourAmount(debour.getNonTaxableAmount());
                                                nonTaxableDebour.setPaymentDateTime(debour.getPaymentDateTime());
                                                nonTaxableDebour.setPaymentType(debour.getPaymentType());
                                                nonTaxableDebour.setProvision(provision);
                                                debourService.addOrUpdateDebour(nonTaxableDebour);

                                                InvoiceItem invoiceItem = getInvoiceItemFromDebour(nonTaxableDebour,
                                                        true);
                                                invoiceItemService.addOrUpdateInvoiceItem(invoiceItem);
                                                invoice.getInvoiceItems().add(invoiceItem);
                                                nonTaxableDebour.setInvoiceItem(invoiceItem);

                                                debour.setDebourAmount(
                                                        debour.getDebourAmount() - debour.getNonTaxableAmount());
                                                debourService.addOrUpdateDebour(nonTaxableDebour);
                                            }

                                            InvoiceItem invoiceItem = getInvoiceItemFromDebour(debour,
                                                    debour.getBillingType().getIsNonTaxable());
                                            invoiceItemService.addOrUpdateInvoiceItem(invoiceItem);
                                            invoice.getInvoiceItems().add(invoiceItem);
                                            debour.setInvoiceItem(invoiceItem);
                                            debour.setProvision(provision);
                                            debourService.addOrUpdateDebour(debour);

                                            invoice.setManualPaymentType(debour.getPaymentType());

                                            debour.setNonTaxableAmount(null);
                                            debourService.addOrUpdateDebour(debour);
                                        }
                                    }
                }
            }

            Integer nbrOfDayFromDueDate = 30;
            if (invoice.getDueDate() == null)
                invoice.setDueDate(LocalDate.now().plusDays(nbrOfDayFromDueDate));

        }

        // Defined billing label
        if (!invoice.getIsInvoiceFromProvider()
                && !constantService.getBillingLabelTypeOther().getId().equals(invoice.getBillingLabelType().getId())) {

            ITiers customerOrder = invoice.getTiers();
            if (invoice.getResponsable() != null)
                customerOrder = invoice.getResponsable();
            if (invoice.getConfrere() != null)
                customerOrder = invoice.getConfrere();
            Document billingDocument = documentService.getBillingDocument(customerOrder.getDocuments());

            invoiceHelper.setInvoiceLabel(invoice, billingDocument, null, customerOrder);
        }

        if (isNewInvoice) {
            if (invoice.getIsInvoiceFromProvider())
                invoice.setInvoiceStatus(constantService.getInvoiceStatusReceived());
            else
                invoice.setInvoiceStatus(constantService.getInvoiceStatusSend());
        }

        // Save before to have an ID on invoice
        addOrUpdateInvoice(invoice);

        // Associate invoice to invoice item
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            invoiceItem.setInvoice(invoice);
        }

        invoiceHelper.setPriceTotal(invoice);

        if (isNewInvoice) {
            if (invoice.getIsInvoiceFromProvider())
                accountingRecordService.generateAccountingRecordsForPurshaseOnInvoiceGeneration(invoice);
            else
                accountingRecordService.generateAccountingRecordsForSaleOnInvoiceGeneration(invoice);
        }

        // Do not generate bank transfert if invoice from Competent Authority
        // It's done when debour is filled in provision
        if (invoice.getIsInvoiceFromProvider()
                && invoice.getManualPaymentType().getId().equals(constantService.getPaymentTypeVirement().getId())
                && invoice.getCompetentAuthority() == null)
            invoice.setBankTransfert(bankTransfertService.generateBankTransfertForManualInvoice(invoice));

        addOrUpdateInvoice(invoice);

        if (isNewInvoice && debourPayments.size() > 0) {
            for (Integer paymentId : debourPayments.keySet()) {
                Debour debour = debourPayments.get(paymentId);
                if (debour.getPaymentType().getId().equals(constantService.getPaymentTypeCB().getId())
                        || debour.getPaymentType().getId().equals(constantService.getPaymentTypeCheques().getId()))
                    paymentService.associateOutboundPaymentAndInvoice(paymentService.getPayment(paymentId), invoice,
                            new MutableBoolean(false), null);
            }
        }
        return invoice;
    }

    private InvoiceItem getInvoiceItemFromDebour(Debour debour, boolean isNonTaxable) throws OsirisException {
        InvoiceItem invoiceItem = new InvoiceItem();
        BillingItem billingItem = pricingHelper
                .getAppliableBillingItem(billingItemService.getBillingItemByBillingType(debour.getBillingType()));
        if (billingItem == null)
            throw new OsirisException(null,
                    "No appliable billing item found for billing type n°" + debour.getBillingType().getId());
        invoiceItem.setBillingItem(billingItem);

        invoiceItem.setDiscountAmount(0f);
        invoiceItem.setIsGifted(false);
        invoiceItem.setIsOverridePrice(false);
        invoiceItem.setLabel(debour.getBillingType().getLabel());
        if (isNonTaxable) {
            invoiceItem.setPreTaxPrice(debour.getDebourAmount());
        } else {
            invoiceItem.setPreTaxPrice(
                    debour.getDebourAmount() / ((100 + constantService.getVatDeductible().getRate()) / 100f));
        }
        return invoiceItem;
    }

    private boolean hasAtLeastOneInvoiceItemNotNull(Invoice invoice) {
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems())
            if (invoiceItem.getPreTaxPrice() > 0) {
                return true;
            }
        return false;
    }

    @Override
    public void unletterInvoice(Invoice invoice) throws OsirisException {
        AccountingAccount accountingAccountCustomer = accountingRecordService
                .getCustomerAccountingAccountForInvoice(invoice);

        List<AccountingRecord> accountingRecords = accountingRecordService
                .findByAccountingAccountAndInvoice(accountingAccountCustomer, invoice);

        if (accountingRecords != null)
            for (AccountingRecord accountingRecord : accountingRecords) {
                accountingRecord.setLetteringDateTime(null);
                accountingRecord.setLetteringNumber(null);
                accountingRecordService.addOrUpdateAccountingRecord(accountingRecord);
            }
        invoice.setInvoiceStatus(constantService.getInvoiceStatusSend());
        addOrUpdateInvoice(invoice);
    }

    @Override
    public void sendRemindersForInvoices()
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        List<Invoice> invoices = invoiceRepository.findInvoiceForReminder(constantService.getInvoiceStatusSend());

        if (invoices != null && invoices.size() > 0)
            for (Invoice invoice : invoices) {
                boolean toSend = false;
                if (invoice.getFirstReminderDateTime() == null
                        && invoice.getDueDate().isBefore(LocalDate.now().minusDays(1 * 30))) {
                    toSend = true;
                    invoice.setFirstReminderDateTime(LocalDateTime.now());

                    // Reminder once, next time provision will be mandatory :)
                    ITiers customerOrderToSetProvision = invoiceHelper.getCustomerOrder(invoice);
                    if (customerOrderToSetProvision instanceof Responsable)
                        customerOrderToSetProvision = ((Responsable) customerOrderToSetProvision).getTiers();
                    if (customerOrderToSetProvision instanceof Tiers) {
                        ((Tiers) customerOrderToSetProvision).setIsProvisionalPaymentMandatory(true);
                        tiersService.addOrUpdateTiers((Tiers) customerOrderToSetProvision);
                    } else if (customerOrderToSetProvision instanceof Confrere) {
                        ((Confrere) customerOrderToSetProvision).setIsProvisionalPaymentMandatory(true);
                        confrereService.addOrUpdateConfrere((Confrere) customerOrderToSetProvision);
                    }
                } else if (invoice.getSecondReminderDateTime() == null
                        && invoice.getDueDate().isBefore(LocalDate.now().minusDays(3 * 60))) {
                    toSend = true;
                    invoice.setSecondReminderDateTime(LocalDateTime.now());
                } else if (invoice.getDueDate().isBefore(LocalDate.now().minusDays(6 * 90))) {
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
    public Float getRemainingAmountToPayForInvoice(Invoice invoice) throws OsirisException {
        invoice = getInvoice(invoice.getId());
        if (invoice != null) {
            Float total = invoice.getTotalPrice();

            if (invoice.getPayments() != null && invoice.getPayments().size() > 0)
                for (Payment payment : invoice.getPayments())
                    if (payment.getIsCancelled() == null || !payment.getIsCancelled())
                        total -= payment.getPaymentAmount();

            if (invoice.getDeposits() != null && invoice.getDeposits().size() > 0)
                for (Deposit deposit : invoice.getDeposits())
                    if (deposit.getIsCancelled() == null || !deposit.getIsCancelled())
                        total -= deposit.getDepositAmount();

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
        return newInvoice;
    }

    @Override
    public CustomerOrder getCustomerOrderByIdInvoice(Integer idInvoice) {
        Invoice invoice = getInvoice(idInvoice);
        if (invoice != null)
            return invoice.getCustomerOrder();
        return null;
    }

}