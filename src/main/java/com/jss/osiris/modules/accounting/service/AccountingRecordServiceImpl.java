package com.jss.osiris.modules.accounting.service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.accounting.model.AccountingBalance;
import com.jss.osiris.modules.accounting.model.AccountingBalanceBilan;
import com.jss.osiris.modules.accounting.model.AccountingBalanceSearch;
import com.jss.osiris.modules.accounting.model.AccountingBalanceViewTitle;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.model.AccountingRecordSearch;
import com.jss.osiris.modules.accounting.repository.AccountingRecordRepository;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.quotation.model.Invoice;
import com.jss.osiris.modules.quotation.model.InvoiceItem;
import com.jss.osiris.modules.quotation.service.InvoiceItemService;
import com.jss.osiris.modules.quotation.service.InvoiceService;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;

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

    @Autowired
    AccountingBalanceHelper accountingBalanceHelper;

    @Autowired
    AccountingExportHelper accountingExportHelper;

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

        // If cusomter order is a Responsable, get accounting account of parent Tiers
        if (customerOrder.getAccountingAccountCustomer() == null || (customerOrder instanceof Responsable
                && ((Responsable) customerOrder).getTiers().getAccountingAccountCustomer() == null))
            throw new Exception("No customer accounting account in ITiers " + customerOrder.getId());

        AccountingAccount accountingAccountCustomer = null;
        if (customerOrder instanceof Responsable)
            accountingAccountCustomer = ((Responsable) customerOrder).getTiers().getAccountingAccountCustomer();
        else
            accountingAccountCustomer = customerOrder.getAccountingAccountCustomer();

        Float balance = 0f;
        balance += invoiceService.getPriceTotal(invoice);

        // One write on customer account for all invoice
        generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), null,
                "Commande n°" + invoice.getCustomerOrder().getId(), null,
                balance,
                accountingAccountCustomer, null, invoice, salesJournal);

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

            generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), null,
                    "Commande n°" + invoice.getCustomerOrder().getId() + " - produit "
                            + invoiceItem.getBillingItem().getBillingType().getLabel(),
                    invoiceItem.getPreTaxPrice() - invoiceItem.getDiscountAmount(), null, producAccountingAccount,
                    invoiceItem, invoice, salesJournal);

            balance -= invoiceItem.getPreTaxPrice() - invoiceItem.getDiscountAmount();

            if (invoiceItem.getVat() != null) {
                generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), null,
                        "Commande n°" + invoice.getCustomerOrder().getId() + " - TVA pour le produit "
                                + invoiceItem.getBillingItem().getBillingType().getLabel(),
                        invoiceItem.getVatPrice(), null, invoiceItem.getVat().getAccountingAccount(),
                        invoiceItem, invoice, salesJournal);

                balance -= invoiceItem.getVatPrice();
            }
        }

        // Check balance ok
        if (Math.round(balance * 100) != 0) {
            throw new Exception("Accounting records  are not balanced for invoice " + invoice.getId());
        }
    }

    private AccountingRecord generateNewAccountingRecord(LocalDateTime operationDatetime, Integer operationId,
            String manualAccountingDocumentNumber,
            String label, Float creditAmount, Float debitAmount,
            AccountingAccount accountingAccount, InvoiceItem invoiceItem, Invoice invoice, AccountingJournal journal) {
        AccountingRecord accountingRecord = new AccountingRecord();
        accountingRecord.setOperationDateTime(operationDatetime);
        accountingRecord.setTemporaryOperationId(operationId);
        accountingRecord.setManualAccountingDocumentNumber(manualAccountingDocumentNumber);
        accountingRecord.setLabel(label);
        accountingRecord.setCreditAmount(creditAmount);
        accountingRecord.setDebitAmount(debitAmount);
        accountingRecord.setAccountingAccount(accountingAccount);
        accountingRecord.setIsTemporary(true);
        accountingRecord.setInvoiceItem(invoiceItem);
        accountingRecord.setIsANouveau(false);
        accountingRecord.setInvoice(invoice);
        accountingRecord.setAccountingJournal(journal);
        accountingRecordRepository.save(accountingRecord);
        return accountingRecord;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dailyAccountClosing() {
        List<AccountingJournal> journals = accountingJournalService.getAccountingJournals();

        Integer maxIdOperation = accountingRecordRepository
                .findMaxIdOperationForMinOperationDateTime(LocalDateTime.now().with(ChronoField.DAY_OF_YEAR, 1)
                        .with(ChronoField.HOUR_OF_DAY, 0)
                        .with(ChronoField.MINUTE_OF_DAY, 0).with(ChronoField.SECOND_OF_DAY, 0));

        if (maxIdOperation == null)
            maxIdOperation = 0;
        maxIdOperation++;

        HashMap<Integer, Integer> definitiveIdOperation = new HashMap<Integer, Integer>();

        if (journals != null && journals.size() > 0) {
            for (AccountingJournal accountingJournal : journals) {
                List<AccountingRecord> accountingRecords = accountingRecordRepository
                        .findByAccountingJournalAndIsTemporary(accountingJournal, true);

                Integer maxIdAccounting = accountingRecordRepository
                        .findMaxIdAccountingForAccontingJournalAndMinOperationDateTime(
                                accountingJournal,
                                LocalDateTime.now().with(ChronoField.DAY_OF_YEAR, 1)
                                        .with(ChronoField.HOUR_OF_DAY, 0)
                                        .with(ChronoField.MINUTE_OF_DAY, 0).with(ChronoField.SECOND_OF_DAY, 0));
                if (maxIdAccounting == null)
                    maxIdAccounting = 0;
                maxIdAccounting++;

                if (accountingRecords != null && accountingRecords.size() > 0) {
                    for (AccountingRecord accountingRecord : accountingRecords) {

                        if (definitiveIdOperation.get(accountingRecord.getTemporaryOperationId()) == null) {
                            definitiveIdOperation.put(accountingRecord.getTemporaryOperationId(), maxIdOperation);
                            maxIdOperation++;
                        }
                        accountingRecord.setAccountingDateTime(LocalDateTime.now());
                        accountingRecord.setAccountingId(maxIdAccounting);
                        accountingRecord.setOperationId(maxIdOperation);
                        accountingRecord.setIsTemporary(false);
                        accountingRecordRepository.save(accountingRecord);
                        maxIdAccounting++;
                    }
                }
            }
        }
    }

    @Override
    public List<AccountingRecord> searchAccountingRecords(AccountingRecordSearch accountingRecordSearch) {
        return accountingRecordRepository.searchAccountingRecords(accountingRecordSearch.getAccountingClass(),
                accountingRecordSearch.getAccountingAccount(), accountingRecordSearch.getAccountingJournal(),
                accountingRecordSearch.getStartDate(), accountingRecordSearch.getEndDate());
    }

    @Override
    public List<AccountingBalance> searchAccountingBalance(AccountingBalanceSearch accountingBalanceSearch) {
        Integer accountingAccountId = accountingBalanceSearch.getAccountingAccount() != null
                ? accountingBalanceSearch.getAccountingAccount().getId()
                : 0;
        Integer accountingClassId = accountingBalanceSearch.getAccountingClass() != null
                ? accountingBalanceSearch.getAccountingClass().getId()
                : 0;
        String accountNumber = accountingBalanceSearch.getAccountingAccountNumber() != null
                ? accountingBalanceSearch.getAccountingAccountNumber()
                : "";
        List<AccountingBalance> aa = accountingRecordRepository.searchAccountingBalance(
                accountingClassId,
                accountingAccountId, accountNumber,
                accountingBalanceSearch.getStartDate(), accountingBalanceSearch.getEndDate());

        return aa;
    }

    @Override
    public List<AccountingBalance> searchAccountingBalanceGenerale(AccountingBalanceSearch accountingBalanceSearch) {
        Integer accountingAccountId = accountingBalanceSearch.getAccountingAccount() != null
                ? accountingBalanceSearch.getAccountingAccount().getId()
                : 0;
        Integer accountingClassId = accountingBalanceSearch.getAccountingClass() != null
                ? accountingBalanceSearch.getAccountingClass().getId()
                : 0;
        String accountNumber = accountingBalanceSearch.getAccountingAccountNumber() != null
                ? accountingBalanceSearch.getAccountingAccountNumber()
                : "";
        return accountingRecordRepository.searchAccountingBalanceGenerale(
                accountingClassId,
                accountingAccountId, accountNumber,
                accountingBalanceSearch.getStartDate(), accountingBalanceSearch.getEndDate());

    }

    @Override
    public List<AccountingBalanceViewTitle> getBilan(LocalDateTime startDate, LocalDateTime endDate) {
        List<AccountingBalanceBilan> accountingRecords = accountingRecordRepository
                .getAccountingRecordAggregateByAccountingNumber(startDate, endDate);

        List<AccountingBalanceBilan> accountingRecordsN1 = accountingRecordRepository
                .getAccountingRecordAggregateByAccountingNumber(startDate.minusYears(1), endDate.minusYears(1));

        ArrayList<AccountingBalanceViewTitle> outBilan = new ArrayList<AccountingBalanceViewTitle>();

        outBilan.add(accountingBalanceHelper.getBilanActif(accountingRecords, accountingRecordsN1));
        outBilan.add(accountingBalanceHelper.getBilanPassif(accountingRecords, accountingRecordsN1));

        return outBilan;
    }

    @Override
    public List<AccountingBalanceViewTitle> getProfitAndLost(LocalDateTime startDate, LocalDateTime endDate) {
        List<AccountingBalanceBilan> accountingRecords = accountingRecordRepository
                .getAccountingRecordAggregateByAccountingNumber(startDate, endDate);

        List<AccountingBalanceBilan> accountingRecordsN1 = accountingRecordRepository
                .getAccountingRecordAggregateByAccountingNumber(startDate.minusYears(1), endDate.minusYears(1));

        return accountingBalanceHelper.getProfitAndLost(accountingRecords, accountingRecordsN1);
    }

    @Override
    public File getGrandLivreExport(AccountingAccountClass accountingClass, LocalDateTime startDate,
            LocalDateTime endDate)
            throws Exception {
        return accountingExportHelper.getGrandLivre(accountingClass, startDate, endDate);
    }

    @Override
    public File getJournalExport(AccountingJournal accountingJournal, LocalDateTime startDate, LocalDateTime endDate)
            throws Exception {
        return accountingExportHelper.getJournal(accountingJournal, startDate, endDate);
    }

    @Override
    public File getAccountingAccountExport(AccountingAccount accountingAccount, LocalDateTime startDate,
            LocalDateTime endDate) throws Exception {
        return accountingExportHelper.getAccountingAccount(accountingAccount, startDate, endDate);
    }

    @Override
    public File getProfitLostExport(LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        return accountingExportHelper.getProfitAndLost(this.getProfitAndLost(startDate, endDate));
    }

    @Override
    public File getBilanExport(LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        List<AccountingBalanceBilan> accountingRecords = accountingRecordRepository
                .getAccountingRecordAggregateByAccountingNumber(startDate, endDate);

        List<AccountingBalanceBilan> accountingRecordsN1 = accountingRecordRepository
                .getAccountingRecordAggregateByAccountingNumber(startDate.minusYears(1), endDate.minusYears(1));

        ArrayList<AccountingBalanceViewTitle> outBilanActif = new ArrayList<AccountingBalanceViewTitle>();

        outBilanActif.add(accountingBalanceHelper.getBilanActif(accountingRecords, accountingRecordsN1));

        ArrayList<AccountingBalanceViewTitle> outBilanPassif = new ArrayList<AccountingBalanceViewTitle>();

        outBilanPassif.add(accountingBalanceHelper.getBilanPassif(accountingRecords, accountingRecordsN1));

        return accountingExportHelper.getBilan(outBilanActif, outBilanPassif);
    }

    @Override
    public File getAccountingBalanceExport(Integer accountingClassId, String accountingAccountNumber,
            Integer accountingAccountId, LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        List<AccountingBalance> accountingBalanceRecords = accountingRecordRepository.searchAccountingBalance(
                accountingClassId != null ? accountingClassId : 0,
                accountingAccountId != null ? accountingAccountId : 0,
                (accountingAccountNumber != null && !accountingAccountNumber.equals("") ? accountingAccountNumber : ""),
                startDate, endDate);
        return accountingExportHelper.getBalance(accountingBalanceRecords, false);
    }

    @Override
    public File getAccountingBalanceGeneraleExport(Integer accountingClassId, String accountingAccountNumber,
            Integer accountingAccountId, LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        List<AccountingBalance> accountingBalanceRecords = accountingRecordRepository.searchAccountingBalanceGenerale(
                accountingClassId != null ? accountingClassId : 0,
                accountingAccountId != null ? accountingAccountId : 0,
                (accountingAccountNumber != null && !accountingAccountNumber.equals("") ? accountingAccountNumber : ""),
                startDate, endDate);
        return accountingExportHelper.getBalance(accountingBalanceRecords, true);
    }
}
