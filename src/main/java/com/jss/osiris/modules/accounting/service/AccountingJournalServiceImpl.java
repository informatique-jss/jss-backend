package com.jss.osiris.modules.accounting.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.repository.AccountingJournalRepository;

@Service
public class AccountingJournalServiceImpl implements AccountingJournalService {

    private static final Logger logger = LoggerFactory.getLogger(AccountingJournalServiceImpl.class);

    @Autowired
    AccountingJournalRepository accountingJournalRepository;

    @Value("${accounting.journal.code.sales}")
    String salesAccountingJournalCode;

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

    @Override
    public AccountingJournal getAccountingJournalByCode(String code) {
        return accountingJournalRepository.findByCode(code);
    }

    @Override
    public AccountingJournal getSalesAccountingJournal() throws Exception {
        AccountingJournal salesJournal = this.getAccountingJournalByCode(salesAccountingJournalCode);
        if (salesJournal == null)
            logger.error("Unable to find accounting journal for code " + salesAccountingJournalCode);
        return salesJournal;
    }
}
