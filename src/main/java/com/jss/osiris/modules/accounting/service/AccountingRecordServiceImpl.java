package com.jss.osiris.modules.accounting.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.repository.AccountingRecordRepository;
import com.jss.osiris.modules.quotation.model.Invoice;

@Service
public class AccountingRecordServiceImpl implements AccountingRecordService {

    @Autowired
    AccountingRecordRepository accountingRecordRepository;

    @Override
    @Cacheable(value = "accountingRecordList", key = "#root.methodName")
    public List<AccountingRecord> getAccountingRecords() {
        return IterableUtils.toList(accountingRecordRepository.findAll());
    }

    @Override
    @Cacheable(value = "accountingRecord", key = "#id")
    public AccountingRecord getAccountingRecord(Integer id) {
        Optional<AccountingRecord> accountingRecord = accountingRecordRepository.findById(id);
        if (!accountingRecord.isEmpty())
            return accountingRecord.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "accountingRecordList", allEntries = true),
            @CacheEvict(value = "accountingRecord", key = "#accountingRecord.id")
    })
    public AccountingRecord addOrUpdateAccountingRecord(
            AccountingRecord accountingRecord) {
        return accountingRecordRepository.save(accountingRecord);
    }

    public boolean writeAccountingRecordForInvoiceIssuance(Invoice invoice) {

        return false;
    }
}
