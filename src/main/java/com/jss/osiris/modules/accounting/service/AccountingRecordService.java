package com.jss.osiris.modules.accounting.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingBalance;
import com.jss.osiris.modules.accounting.model.AccountingBalanceSearch;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.model.AccountingRecordSearch;
import com.jss.osiris.modules.accounting.model.AccountingRecordSearchResult;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.quotation.model.BankTransfert;

public interface AccountingRecordService {
        public AccountingRecord getAccountingRecord(Integer id);

        public List<AccountingRecord> getAccountingRecordsByTemporaryOperationId(Integer id);

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
                        AccountingRecordSearch accountingRecordSearch, boolean fetchAll);

        public List<AccountingBalance> searchAccountingBalance(AccountingBalanceSearch accountingBalanceSearch);

        public List<AccountingBalance> searchAccountingBalanceGenerale(AccountingBalanceSearch accountingBalanceSearch);

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

}
