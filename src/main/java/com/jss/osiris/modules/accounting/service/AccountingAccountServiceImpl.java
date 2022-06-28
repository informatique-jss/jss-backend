package com.jss.osiris.modules.accounting.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.repository.AccountingAccountRepository;

@Service
public class AccountingAccountServiceImpl implements AccountingAccountService {

    @Autowired
    AccountingAccountRepository accountingAccountRepository;

    @Override
    public List<AccountingAccount> getAccountingAccounts() {
        return IterableUtils.toList(accountingAccountRepository.findAll());
    }

    @Override
    public AccountingAccount getAccountingAccount(Integer id) {
        Optional<AccountingAccount> accountingAccount = accountingAccountRepository.findById(id);
        if (!accountingAccount.isEmpty())
            return accountingAccount.get();
        return null;
    }

    @Override
    public AccountingAccount addOrUpdateAccountingAccount(
            AccountingAccount accountingAccount) {
        return accountingAccountRepository.save(accountingAccount);
    }

    @Override
    public List<AccountingAccount> getAccountingAccountByLabel(String label) {
        return accountingAccountRepository.findByLabelContainingIgnoreCase(label);
    }
}
