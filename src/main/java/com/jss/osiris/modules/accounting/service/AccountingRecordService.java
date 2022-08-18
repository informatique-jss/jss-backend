package com.jss.osiris.modules.accounting.service;

import java.util.List;

import com.jss.osiris.modules.accounting.model.AccountingRecord;

public interface AccountingRecordService {
    public List<AccountingRecord> getAccountingRecords();

    public AccountingRecord getAccountingRecord(Integer id);
	
	 public AccountingRecord addOrUpdateAccountingRecord(AccountingRecord accountingRecord);
}
