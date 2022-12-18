package com.jss.osiris.modules.accounting.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.repository.AccountingJournalRepository;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;

@Service
public class AccountingJournalServiceImpl implements AccountingJournalService {

    @Autowired
    AccountingJournalRepository accountingJournalRepository;

    @Autowired
    ConstantService constantService;

    @Override
    @Cacheable(value = "accountingJournalList", key = "#root.methodName")
    public List<AccountingJournal> getAccountingJournals() {
        return IterableUtils.toList(accountingJournalRepository.findAll());
    }

    @Override
    @Cacheable(value = "accountingJournal", key = "#id")
    public AccountingJournal getAccountingJournal(Integer id) {
        Optional<AccountingJournal> accountingJournal = accountingJournalRepository.findById(id);
        if (accountingJournal.isPresent())
            return accountingJournal.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "accountingJournalList", allEntries = true),
            @CacheEvict(value = "accountingJournal", key = "#accountingJournal.id")
    })
    @Transactional(rollbackFor = Exception.class)
    public AccountingJournal addOrUpdateAccountingJournal(
            AccountingJournal accountingJournal) {
        return accountingJournalRepository.save(accountingJournal);
    }

    @Override
    public AccountingJournal getAccountingJournalByCode(String code) {
        return accountingJournalRepository.findByCode(code);
    }

    @Override
    public AccountingJournal getSalesAccountingJournal() throws OsirisException {
        AccountingJournal salesJournal = constantService.getAccountingJournalSales();
        if (salesJournal == null)
            throw new OsirisException(null, "Unable to find accounting journal Sales. Check constants");
        return salesJournal;
    }

    @Override
    public AccountingJournal getPurchasesAccountingJournal() throws OsirisException {
        AccountingJournal purchasesJournal = constantService.getAccountingJournalPurchases();
        if (purchasesJournal == null)
            throw new OsirisException(null, "Unable to find accounting journal Purchases. Check constants");
        return purchasesJournal;
    }

    @Override
    public AccountingJournal getANouveauAccountingJournal() throws OsirisException {
        AccountingJournal aNouveauJournal = constantService.getAccountingJournalANouveau();
        if (aNouveauJournal == null)
            throw new OsirisException(null, "Unable to find accounting journal A Nouveau. Check constants");
        return aNouveauJournal;
    }
}
