package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.accounting.model.AccountingAccountCouple;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.miscellaneous.model.Provider;
import com.jss.osiris.modules.miscellaneous.repository.ProviderRepository;

@Service
public class ProviderServiceImpl implements ProviderService {

    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Override
    @Cacheable(value = "providerList", key = "#root.methodName")
    public List<Provider> getProviders() {
        return IterableUtils.toList(providerRepository.findAll());
    }

    @Override
    @Cacheable(value = "provider", key = "#id")
    public Provider getProvider(Integer id) {
        Optional<Provider> provider = providerRepository.findById(id);
        if (!provider.isEmpty())
            return provider.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "providerList", allEntries = true),
            @CacheEvict(value = "provider", key = "#provider.id")
    })
    public Provider addOrUpdateProvider(Provider provider) throws Exception {
        // Generate accounting accounts
        if (provider.getId() == null
                || provider.getAccountingAccountCustomer() == null && provider.getAccountingAccountProvider() == null) {
            AccountingAccountCouple accountingAccountCouple = accountingAccountService
                    .generateAccountingAccountsForEntity(provider.getLabel());
            provider.setAccountingAccountCustomer(accountingAccountCouple.getAccountingAccountCustomer());
            provider.setAccountingAccountProvider(accountingAccountCouple.getAccountingAccountProvider());
        }

        return providerRepository.save(provider);
    }
}
