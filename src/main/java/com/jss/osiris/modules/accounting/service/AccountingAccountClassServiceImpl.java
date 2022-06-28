package com.jss.osiris.modules.accounting.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.accounting.repository.AccountingAccountClassRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (!accountingAccountClass.isEmpty())
            return accountingAccountClass.get();
        return null;
    }
	
	 @Override
    public AccountingAccountClass addOrUpdateAccountingAccountClass(
            AccountingAccountClass accountingAccountClass) {
        return accountingAccountClassRepository.save(accountingAccountClass);
    }
}
