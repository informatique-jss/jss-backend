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

import com.jss.osiris.modules.accounting.model.PrincipalAccountingAccount;
import com.jss.osiris.modules.accounting.repository.PrincipalAccountingAccountRepository;

@Service
public class PrincipalAccountingAccountServiceImpl implements PrincipalAccountingAccountService {

    @Autowired
    PrincipalAccountingAccountRepository principalAccountingAccountRepository;

    @Override
    @Cacheable(value = "principalAccountingAccountList", key = "#root.methodName")
    public List<PrincipalAccountingAccount> getPrincipalAccountingAccounts() {
        return IterableUtils.toList(principalAccountingAccountRepository.findAll());
    }

    @Override
    @Cacheable(value = "principalAccountingAccount", key = "#id")
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
    @Caching(evict = {
            @CacheEvict(value = "principalAccountingAccountList", allEntries = true),
            @CacheEvict(value = "principalAccountingAccount", key = "#principalAccountingAccount.id")
    })
    @Transactional(rollbackFor = Exception.class)
    public PrincipalAccountingAccount addOrUpdatePrincipalAccountingAccount(
            PrincipalAccountingAccount principalAccountingAccount) {
        return principalAccountingAccountRepository.save(principalAccountingAccount);
    }
}
