package com.jss.osiris.modules.accounting.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.repository.AccountingRecordRepository;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.quotation.model.Invoice;
import com.jss.osiris.modules.quotation.model.InvoiceItem;
import com.jss.osiris.modules.quotation.service.InvoiceItemService;
import com.jss.osiris.modules.quotation.service.InvoiceService;
import com.jss.osiris.modules.tiers.model.ITiers;

@Service
public class AccountingRecordServiceImpl implements AccountingRecordService {

    @Autowired
    AccountingRecordRepository accountingRecordRepository;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Autowired
    VatService vatService;

    @Autowired
    AccountingJournalService accountingJournalService;

    @Override
    @Cacheable(value = "accountingRecordList", key = "#root.methodName")
    public List<AccountingRecord> getAccountingRecords() {
        return IterableUtils.toList(accountingRecordRepository.findAll());
    }

    @Override
    @Cacheable(value = "accountingRecord", key = "#id")
    public AccountingRecord getAccountingRecord(Integer id) {
        Optional<AccountingRecord> accountingRecord = accountingRecordRepository.findById(id);
        if (!accountingRecord.isEmpty())
            return accountingRecord.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "accountingRecordList", allEntries = true),
            @CacheEvict(value = "accountingRecord", key = "#accountingRecord.id")
    })
    public AccountingRecord addOrUpdateAccountingRecord(
            AccountingRecord accountingRecord) {
        return accountingRecordRepository.save(accountingRecord);
    }

    public boolean writeAccountingRecordForInvoiceIssuance(Invoice invoice) {

        return false;
    }

    public void generateAccountingRecordsForSaleOnInvoiceGeneration(Invoice invoice) throws Exception {
        AccountingJournal salesJournal = accountingJournalService.getSalesAccountingJournal();

        if (invoice == null)
            throw new Exception("No invoice provided");

        if (invoice.getCustomerOrder() == null)
            throw new Exception("No customer order in invoice " + invoice.getId());

        ITiers customerOrder = invoiceService.getCustomerOrder(invoice);

        if (customerOrder.getAccountingAccountCustomer() == null)
            throw new Exception("No customer accounting account in ITiers " + customerOrder.getId());

        // One write on customer account for all invoice
        generateNewAccountingRecord(LocalDateTime.now(), null,
                "Commande n°" + invoice.getCustomerOrder().getId(), null,
                invoiceService.getPriceTotal(invoice),
                customerOrder.getAccountingAccountCustomer(), null, invoice, salesJournal);

        // For each invoice item, one write on product and VAT account for each invoice
        // item
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            if (invoiceItem.getBillingItem() == null)
                throw new Exception("No billing item defined in invoice item n°" + invoiceItem.getId());

            if (invoiceItem.getBillingItem().getBillingType() == null)
                throw new Exception(
                        "No billing type defined in billing item n°" + invoiceItem.getBillingItem().getId());

            AccountingAccount producAccountingAccount = accountingAccountService
                    .getProductAccountingAccountFromAccountingAccountList(
                            invoiceItem.getBillingItem().getAccountingAccounts());

            if (producAccountingAccount == null)
                throw new Exception("No product accounting account defined in billing item n°"
                        + invoiceItem.getBillingItem().getId());

            generateNewAccountingRecord(LocalDateTime.now(), null,
                    "Commande n°" + invoice.getCustomerOrder().getId() + " - produit "
                            + invoiceItem.getBillingItem().getBillingType().getLabel(),
                    invoiceItem.getPreTaxPrice() - invoiceItem.getDiscountAmount(), null, producAccountingAccount,
                    invoiceItem, invoice, salesJournal);

            if (invoiceItem.getVat() != null)
                generateNewAccountingRecord(LocalDateTime.now(), null,
                        "Commande n°" + invoice.getCustomerOrder().getId() + " - TVA pour le produit "
                                + invoiceItem.getBillingItem().getBillingType().getLabel(),
                        invoiceItem.getVatPrice(), null, invoiceItem.getVat().getAccountingAccount(),
                        invoiceItem, invoice, salesJournal);
        }
    }

    private AccountingRecord generateNewAccountingRecord(LocalDateTime operationDatetime,
            String manualAccountingDocumentNumber,
            String label, Float creditAmount, Float debitAmount,
            AccountingAccount accountingAccount, InvoiceItem invoiceItem, Invoice invoice, AccountingJournal journal) {
        AccountingRecord accountingRecord = new AccountingRecord();
        accountingRecord.setOperationDateTime(operationDatetime);
        accountingRecord.setManualAccountingDocumentNumber(manualAccountingDocumentNumber);
        accountingRecord.setLabel(label);
        accountingRecord.setCreditAmount(creditAmount);
        accountingRecord.setDebitAmount(debitAmount);
        accountingRecord.setAccountingAccount(accountingAccount);
        accountingRecord.setIsTemporary(true);
        accountingRecord.setInvoiceItem(invoiceItem);
        accountingRecord.setInvoice(invoice);
        accountingRecord.setAccountingJournal(journal);
        accountingRecordRepository.save(accountingRecord);
        return accountingRecord;
    }
}