package com.jss.osiris.modules.accounting.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.repository.AccountingJournalRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountingJournalServiceImpl implements AccountingJournalService {

    @Autowired
    AccountingJournalRepository accountingJournalRepository;

    @Override
	@Cacheable(value = "accountingJournalList", key = "#root.methodName")
    public List<AccountingJournal> getAccountingJournals() {
        return IterableUtils.toList(accountingJournalRepository.findAll());
    }

    @Override
	@Cacheable(value = "accountingJournal", key = "#id")
    public AccountingJournal getAccountingJournal(Integer id) {
        Optional<AccountingJournal> accountingJournal = accountingJournalRepository.findById(id);
        if (!accountingJournal.isEmpty())
            return accountingJournal.get();
        return null;
    }
	
	 @Override
	 @Caching(evict = {
            @CacheEvict(value = "accountingJournalList", allEntries = true),
            @CacheEvict(value = "accountingJournal", key = "#accountingJournal.id")
    })
    public AccountingJournal addOrUpdateAccountingJournal(
            AccountingJournal accountingJournal) {
        return accountingJournalRepository.save(accountingJournal);
    }
}
