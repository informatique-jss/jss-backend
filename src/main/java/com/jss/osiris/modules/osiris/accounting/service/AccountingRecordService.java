package com.jss.osiris.modules.osiris.accounting.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccount;
import com.jss.osiris.modules.osiris.accounting.model.AccountingBalance;
import com.jss.osiris.modules.osiris.accounting.model.AccountingBalanceSearch;
import com.jss.osiris.modules.osiris.accounting.model.AccountingBalanceViewTitle;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecord;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecordSearch;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecordSearchResult;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.model.Refund;
import com.jss.osiris.modules.osiris.quotation.model.BankTransfert;

public interface AccountingRecordService {
        public AccountingRecord getAccountingRecord(Integer id);

        public List<AccountingRecord> getAccountingRecordsByTemporaryOperationId(Integer id);

        public Number getAccountingRecordBalanceByAccountingAccountId(Integer id);

        public Number getAccountingRecordBalanceByAccountingAccountId(Integer id, LocalDateTime accountingDate);

        public Number getBankTransfertTotal(LocalDateTime accountingDate);

        public Number getRefundTotal(LocalDateTime accountingDate);

        public Number getCheckTotal(LocalDateTime accountingDate);

        public Number getDirectDebitTransfertTotal(LocalDateTime accountingDate);

        public void deleteDuplicateAccountingRecord();

        public List<AccountingRecord> addOrUpdateAccountingRecords(List<AccountingRecord> accountingRecords)
                        throws OsirisException;

        public AccountingRecord addOrUpdateAccountingRecord(AccountingRecord accountingRecord,
                        boolean byPassOperationDateTimeCheck) throws OsirisException;

        public void dailyAccountClosing() throws OsirisException;

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

        public File getBillingClosureReceiptFile(Integer tiersId, Integer responsableId, boolean downloadFile)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        // Front search method
        public List<AccountingRecordSearchResult> searchAccountingRecords(
                        AccountingRecordSearch accountingRecordSearch, boolean fetchAll) throws OsirisException;

        public List<AccountingBalance> searchAccountingBalance(AccountingBalanceSearch accountingBalanceSearch)
                        throws OsirisException;

        public List<AccountingBalance> searchAccountingBalanceGenerale(AccountingBalanceSearch accountingBalanceSearch)
                        throws OsirisException;

        public File getGrandLivreExport(AccountingRecordSearch accountingRecordSearch) throws OsirisException;

        public File getJournalExport(AccountingRecordSearch accountingRecordSearch)
                        throws OsirisException;

        public File getAccountingAccountExport(AccountingRecordSearch accountingRecordSearch) throws OsirisException;

        public File getAccountingBalanceExport(AccountingBalanceSearch accountingRecordSearch)
                        throws OsirisException;

        public File getAccountingBalanceGeneraleExport(AccountingBalanceSearch accountingRecordSearch)
                        throws OsirisException;

        public Boolean deleteAccountingRecords(AccountingRecord accountingRecord) throws OsirisValidationException;

        public Boolean letterRecordsForAs400(List<AccountingRecord> accountingRecords)
                        throws OsirisValidationException, OsirisClientMessageException, OsirisException;

        public List<AccountingRecord> getClosedAccountingRecordsForPayment(Payment payment);

        public List<AccountingBalanceViewTitle> getBilan(LocalDateTime startDate, LocalDateTime endDate);

        public List<AccountingBalanceViewTitle> getProfitAndLost(LocalDateTime startDate, LocalDateTime endDate);

        public File getProfitLostExport(LocalDateTime startDate, LocalDateTime endDate) throws OsirisException;

        public File getBilanExport(LocalDateTime startDate, LocalDateTime endDate) throws OsirisException;

}
