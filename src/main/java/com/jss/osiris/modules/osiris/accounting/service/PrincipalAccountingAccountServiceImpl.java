package com.jss.osiris.modules.osiris.accounting.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.accounting.model.PrincipalAccountingAccount;
import com.jss.osiris.modules.osiris.accounting.repository.PrincipalAccountingAccountRepository;

@Service
public class PrincipalAccountingAccountServiceImpl implements PrincipalAccountingAccountService {

    @Autowired
    PrincipalAccountingAccountRepository principalAccountingAccountRepository;

    @Override
    public List<PrincipalAccountingAccount> getPrincipalAccountingAccounts() {
        return IterableUtils.toList(principalAccountingAccountRepository.findAll());
    }

    @Override
    public PrincipalAccountingAccount getPrincipalAccountingAccount(Integer id) {
        Optional<PrincipalAccountingAccount> principalAccountingAccount = principalAccountingAccountRepository
                .findById(id);
        if (principalAccountingAccount.isPresent())
            return principalAccountingAccount.get();
        return null;
    }

    @Override
    public PrincipalAccountingAccount getPrincipalAccountingAccountByCode(String code) {
        return principalAccountingAccountRepository.findByCode(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrincipalAccountingAccount addOrUpdatePrincipalAccountingAccount(
            PrincipalAccountingAccount principalAccountingAccount) {
        return principalAccountingAccountRepository.save(principalAccountingAccount);
    }
}
