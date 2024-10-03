package com.jss.osiris.modules.osiris.accounting.service;

import java.util.List;

import com.jss.osiris.modules.osiris.accounting.model.AccountingJournal;

public interface AccountingJournalService {
    public List<AccountingJournal> getAccountingJournals();

    public AccountingJournal getAccountingJournal(Integer id);

    public AccountingJournal addOrUpdateAccountingJournal(AccountingJournal accountingJournal);

    public AccountingJournal getAccountingJournalByCode(String code);

}
