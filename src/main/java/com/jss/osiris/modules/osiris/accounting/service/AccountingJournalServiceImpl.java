package com.jss.osiris.modules.osiris.accounting.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.accounting.model.AccountingJournal;
import com.jss.osiris.modules.osiris.accounting.repository.AccountingJournalRepository;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;

@Service
public class AccountingJournalServiceImpl implements AccountingJournalService {

    @Autowired
    AccountingJournalRepository accountingJournalRepository;

    @Autowired
    ConstantService constantService;

    @Override
    public List<AccountingJournal> getAccountingJournals() {
        return IterableUtils.toList(accountingJournalRepository.findAll());
    }

    @Override
    public AccountingJournal getAccountingJournal(Integer id) {
        Optional<AccountingJournal> accountingJournal = accountingJournalRepository.findById(id);
        if (accountingJournal.isPresent())
            return accountingJournal.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountingJournal addOrUpdateAccountingJournal(
            AccountingJournal accountingJournal) {
        return accountingJournalRepository.save(accountingJournal);
    }

    @Override
    public AccountingJournal getAccountingJournalByCode(String code) {
        return accountingJournalRepository.findByCode(code);
    }
}
