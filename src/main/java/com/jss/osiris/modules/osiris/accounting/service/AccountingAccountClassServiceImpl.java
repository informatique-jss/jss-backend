package com.jss.osiris.modules.osiris.accounting.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.osiris.accounting.repository.AccountingAccountClassRepository;

@Service
public class AccountingAccountClassServiceImpl implements AccountingAccountClassService {

    @Autowired
    AccountingAccountClassRepository accountingAccountClassRepository;

    @Override
    public List<AccountingAccountClass> getAccountingAccountClasses() {
        return IterableUtils.toList(accountingAccountClassRepository.findAll());
    }

    @Override
    public AccountingAccountClass getAccountingAccountClass(Integer id) {
        Optional<AccountingAccountClass> accountingAccountClass = accountingAccountClassRepository.findById(id);
        if (accountingAccountClass.isPresent())
            return accountingAccountClass.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountingAccountClass addOrUpdateAccountingAccountClass(
            AccountingAccountClass accountingAccountClass) {
        return accountingAccountClassRepository.save(accountingAccountClass);
    }

    @Override
    public AccountingAccountClass getAccountingAccountClassByCode(String code) {
        return accountingAccountClassRepository.findByCode(code);
    }
}
