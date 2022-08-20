package com.jss.osiris.modules.accounting.service;

import java.util.List;

import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.quotation.model.Invoice;

public interface AccountingRecordService {
    public List<AccountingRecord> getAccountingRecords();

    public AccountingRecord getAccountingRecord(Integer id);

    public AccountingRecord addOrUpdateAccountingRecord(AccountingRecord accountingRecord);

    public void generateAccountingRecordsForSaleOnInvoiceGeneration(Invoice invoice) throws Exception;

    public void dailyAccountClosing();
}
