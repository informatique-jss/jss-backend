package com.jss.osiris.modules.accounting.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.modules.accounting.model.AccountingAccountClass;
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

    public File getGrandLivre(AccountingAccountClass accountingClass, LocalDateTime startDate, LocalDateTime endDate)
            throws Exception;
}
