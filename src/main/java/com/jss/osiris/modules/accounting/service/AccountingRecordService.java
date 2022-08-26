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
import com.jss.osiris.modules.quotation.model.Invoice;

public interface AccountingRecordService {
        public List<AccountingRecord> getAccountingRecords();

        public AccountingRecord getAccountingRecord(Integer id);

        public AccountingRecord addOrUpdateAccountingRecord(AccountingRecord accountingRecord);

        public void generateAccountingRecordsForSaleOnInvoiceGeneration(Invoice invoice) throws Exception;

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
}
