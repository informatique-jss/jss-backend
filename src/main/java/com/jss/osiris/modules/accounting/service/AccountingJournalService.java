package com.jss.osiris.modules.accounting.service;

import java.util.List;

import com.jss.osiris.modules.accounting.model.AccountingJournal;

public interface AccountingJournalService {
    public List<AccountingJournal> getAccountingJournals();

    public AccountingJournal getAccountingJournal(Integer id);

    public AccountingJournal addOrUpdateAccountingJournal(AccountingJournal accountingJournal);

    public AccountingJournal getAccountingJournalByCode(String code);

    public AccountingJournal getSalesAccountingJournal() throws Exception;
}
