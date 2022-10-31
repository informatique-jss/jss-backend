package com.jss.osiris.modules.accounting.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.accounting.model.AccountingBalance;
import com.jss.osiris.modules.accounting.model.AccountingBalanceSearch;
import com.jss.osiris.modules.accounting.model.AccountingBalanceViewTitle;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.model.AccountingRecordSearch;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.tiers.model.ITiers;

public interface AccountingRecordService {
        public List<AccountingRecord> getAccountingRecords();

        public AccountingRecord getAccountingRecord(Integer id);

        public AccountingRecord addOrUpdateAccountingRecord(AccountingRecord accountingRecord);

        public AccountingRecord addOrUpdateAccountingRecordFromUser(AccountingRecord accountingRecord);

        public void generateAccountingRecordsForSaleOnInvoiceGeneration(Invoice invoice) throws Exception;

        public void generateAccountingRecordsForPurshaseOnInvoiceGeneration(Invoice invoice) throws Exception;

        public void dailyAccountClosing();

        public List<AccountingRecord> searchAccountingRecords(AccountingRecordSearch accountingRecordSearch);

        public List<AccountingBalance> searchAccountingBalance(AccountingBalanceSearch accountingBalanceSearch);

        public File getGrandLivreExport(AccountingAccountClass accountingClass, LocalDateTime startDate,
                        LocalDateTime endDate)
                        throws Exception;

        public File getJournalExport(AccountingJournal accountingJournal, LocalDateTime startDate,
                        LocalDateTime endDate)
                        throws Exception;

        public File getAccountingAccountExport(AccountingAccount accountingAccount, LocalDateTime startDate,
                        LocalDateTime endDate)
                        throws Exception;

        public File getProfitLostExport(LocalDateTime startDate, LocalDateTime endDate) throws Exception;

        public File getBilanExport(LocalDateTime startDate, LocalDateTime endDate) throws Exception;

        public List<AccountingBalanceViewTitle> getBilan(LocalDateTime startDate, LocalDateTime endDate);

        public List<AccountingBalanceViewTitle> getProfitAndLost(LocalDateTime startDate, LocalDateTime endDate);

        public List<AccountingBalance> searchAccountingBalanceGenerale(AccountingBalanceSearch accountingRecordSearch);

        public File getAccountingBalanceExport(Integer accountingClassId, String accountingAccountNumber,
                        Integer accountingAccountId, LocalDateTime startDate, LocalDateTime endDate) throws Exception;

        public File getAccountingBalanceGeneraleExport(Integer accountingClassId, String accountingAccountNumber,
                        Integer accountingAccountId, LocalDateTime startDate, LocalDateTime endDate) throws Exception;

        public void generateAccountingRecordsForSaleOnInvoicePayment(Invoice invoice, List<Payment> payments,
                        List<Deposit> deposits, Float amountToUse) throws Exception;

        public void generateAccountingRecordsForDepositAndInvoice(Deposit deposit, Invoice invoice, Payment payment)
                        throws Exception;

        public void generateAccountingRecordsForDepositAndCustomerOrder(Deposit deposit, CustomerOrder customerOrder,
                        Payment payment)
                        throws Exception;

        public AccountingAccount getCustomerAccountingAccountForInvoice(Invoice invoice) throws Exception;

        public AccountingAccount getCustomerAccountingAccountForITiers(ITiers tiers) throws Exception;

        public AccountingAccount getProviderAccountingAccountForInvoice(Invoice invoice) throws Exception;

        public AccountingAccount getProviderAccountingAccountForITiers(ITiers tiers) throws Exception;

        public void generateAccountingRecordsForWaintingPayment(Payment payment) throws Exception;

        public void generateBankAccountingRecordsForPayment(Payment payment) throws Exception;

        public Float getRemainingAmountToPayForInvoice(Invoice invoice) throws Exception;

        public Float getRemainingAmountToPayForCustomerOrder(CustomerOrder customerOrder) throws Exception;

        public void generateAccountingRecordsForRefund(Refund refund) throws Exception;

        public List<AccountingRecord> getAccountingRecordsByTemporaryOperationId(Integer operationId) throws Exception;

        public List<AccountingRecord> getAccountingRecordsByOperationId(Integer operationId) throws Exception;

        public void generateAppointForPayment(Payment payment, float remainingMoney, ITiers customerOrder)
                        throws Exception;

        public List<AccountingRecord> addOrUpdateAccountingRecords(List<AccountingRecord> accountingRecords);

        public List<AccountingRecord> findByAccountingAccountAndInvoice(AccountingAccount accountingAccount,
                        Invoice invoice);

        public void generateCounterPart(AccountingRecord originalAccountingRecord);

        public void deleteAccountingRecord(AccountingRecord accountingRecord);

}
