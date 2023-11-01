package com.jss.osiris.modules.accounting.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.accounting.model.AccountingBalance;
import com.jss.osiris.modules.accounting.model.AccountingBalanceSearch;
import com.jss.osiris.modules.accounting.model.AccountingBalanceViewTitle;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.model.AccountingRecordSearch;
import com.jss.osiris.modules.accounting.model.AccountingRecordSearchResult;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.quotation.model.BankTransfert;

public interface AccountingRecordService {
        public AccountingRecord getAccountingRecord(Integer id);

        public List<AccountingRecord> addOrUpdateAccountingRecords(List<AccountingRecord> accountingRecords);

        public AccountingRecord addOrUpdateAccountingRecord(AccountingRecord accountingRecord);

        public void dailyAccountClosing();

        public void deleteAccountingRecord(AccountingRecord accountingRecord);

        public Integer findMaxLetteringNumberForMinLetteringDateTime(LocalDateTime minLetteringDateTime);

        public List<AccountingRecord> findByAccountingAccountAndInvoice(AccountingAccount accountingAccountCustomer,
                        Invoice invoice);

        public List<AccountingRecord> findByAccountingAccountAndRefund(AccountingAccount accountingAccount,
                        Refund refund);

        public List<AccountingRecord> findByAccountingAccountAndBankTransfert(AccountingAccount accountingAccount,
                        BankTransfert bankTransfert);

        // Billing closure generation
        public void sendBillingClosureReceipt()
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public File getBillingClosureReceiptFile(Integer tiersId, boolean downloadFile)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        // Front search method
        public List<AccountingRecordSearchResult> searchAccountingRecords(
                        AccountingRecordSearch accountingRecordSearch);

        public List<AccountingBalance> searchAccountingBalance(AccountingBalanceSearch accountingBalanceSearch);

        public List<AccountingBalance> searchAccountingBalanceGenerale(AccountingBalanceSearch accountingBalanceSearch);

        // Bilan and profit and lost
        public List<AccountingBalanceViewTitle> getBilan(LocalDateTime startDate, LocalDateTime endDate);

        public List<AccountingBalanceViewTitle> getProfitAndLost(LocalDateTime startDate, LocalDateTime endDate);

        public File getGrandLivreExport(AccountingAccountClass accountingClass, LocalDateTime startDate,
                        LocalDateTime endDate) throws OsirisException;

        public File getJournalExport(AccountingJournal accountingJournal, LocalDateTime startDate,
                        LocalDateTime endDate)
                        throws OsirisException;

        public File getAccountingAccountExport(AccountingAccount accountingAccount, LocalDateTime startDate,
                        LocalDateTime endDate) throws OsirisException;

        public File getProfitLostExport(LocalDateTime startDate, LocalDateTime endDate) throws OsirisException;

        public File getBilanExport(LocalDateTime startDate, LocalDateTime endDate) throws OsirisException;

        public File getAccountingBalanceExport(Integer accountingClassId, Integer principalAccountingAccountId,
                        Integer accountingAccountId, LocalDateTime startDate, LocalDateTime endDate)
                        throws OsirisException;

        public File getAccountingBalanceGeneraleExport(Integer accountingClassId, Integer principalAccountingAccountId,
                        Integer accountingAccountId, LocalDateTime startDate, LocalDateTime endDate)
                        throws OsirisException;

}
