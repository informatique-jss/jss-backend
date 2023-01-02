package com.jss.osiris.modules.invoicing.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.InvoiceSearch;
import com.jss.osiris.modules.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.invoicing.repository.InvoiceRepository;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.service.ConfrereService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
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
    public Invoice cancelInvoice(Invoice invoice) throws OsirisException {
        unletterInvoice(invoice);
        if (invoice.getAccountingRecords() != null)
            for (AccountingRecord accountingRecord : invoice.getAccountingRecords()) {
                accountingRecordService.unassociateCustomerOrderPayementAndDeposit(accountingRecord);
                if (!accountingRecord.getIsCounterPart())
                    accountingRecordService.generateCounterPart(accountingRecord);
            }
        invoice.setInvoiceStatus(constantService.getInvoiceStatusCancelled());
        return addOrUpdateInvoice(invoice);
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

        InvoiceStatus statusSent = constantService.getInvoiceStatusSend();

        invoice.setInvoiceStatus(statusSent);
        return this.addOrUpdateInvoice(invoice);
    }

    @Override
    public Invoice getInvoiceForCustomerOrder(Integer customerOrderId) {
        return invoiceRepository.findByCustomerOrderId(customerOrderId);
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

        return invoiceRepository.findInvoice(statusId,
                invoiceSearch.getStartDate().withHour(0).withMinute(0),
                invoiceSearch.getEndDate().withHour(23).withMinute(59), invoiceSearch.getMinAmount(),
                invoiceSearch.getMaxAmount(), invoiceSearch.getShowToRecover(),
                constantService.getInvoiceStatusPayed().getId(), customerOrderId);
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
    public Invoice addOrUpdateInvoiceFromUser(Invoice invoice) throws OsirisException {
        if (!hasAtLeastOneInvoiceItemNotNull(invoice))
            throw new OsirisException(null, "No invoice item found on manual invoice");

        invoice.setCreatedDate(LocalDateTime.now());

        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            invoiceItem.setVatPrice(invoiceItem.getVat().getRate() * invoiceItem.getPreTaxPrice() / 100f);
        }

        Integer nbrOfDayFromDueDate = 30;
        if (invoice.getDueDate() == null)
            invoice.setDueDate(LocalDate.now().plusDays(nbrOfDayFromDueDate));

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

        if (invoice.getIsInvoiceFromProvider())
            invoice.setInvoiceStatus(constantService.getInvoiceStatusReceived());
        else
            invoice.setInvoiceStatus(constantService.getInvoiceStatusSend());

        // Associate invoice to invoice item
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            invoiceItem.setInvoice(invoice);
        }

        invoiceHelper.setPriceTotal(invoice);

        // Save before to have an ID on invoice
        addOrUpdateInvoice(invoice);

        if (invoice.getIsInvoiceFromProvider())
            accountingRecordService.generateAccountingRecordsForPurshaseOnInvoiceGeneration(invoice);
        else
            accountingRecordService.generateAccountingRecordsForSaleOnInvoiceGeneration(invoice);

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
    public void sendRemindersForInvoices() throws OsirisException, OsirisClientMessageException {
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
                    mailHelper.sendCustomerOrderFinalisationToCustomer(invoice.getCustomerOrder(), false, true,
                            invoice.getThirdReminderDateTime() != null);
                    addOrUpdateInvoice(invoice);
                }
            }
    }

}