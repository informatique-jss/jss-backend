package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.accounting.model.AccountingAccountTrouple;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.miscellaneous.model.Provider;
import com.jss.osiris.modules.miscellaneous.repository.ProviderRepository;

@Service
public class ProviderServiceImpl implements ProviderService {

    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Autowired
    MailService mailService;

    @Autowired
    PhoneService phoneService;

    @Override
    @Cacheable(value = "providerList", key = "#root.methodName")
    public List<Provider> getProviders() {
        return IterableUtils.toList(providerRepository.findAll());
    }

    @Override
    @Cacheable(value = "provider", key = "#id")
    public Provider getProvider(Integer id) {
        Optional<Provider> provider = providerRepository.findById(id);
        if (provider.isPresent())
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
                || provider.getAccountingAccountCustomer() == null && provider.getAccountingAccountProvider() == null
                        && provider.getAccountingAccountDeposit() == null) {
            AccountingAccountTrouple accountingAccountCouple = accountingAccountService
                    .generateAccountingAccountsForEntity(provider.getLabel());
            provider.setAccountingAccountCustomer(accountingAccountCouple.getAccountingAccountCustomer());
            provider.setAccountingAccountProvider(accountingAccountCouple.getAccountingAccountProvider());
            provider.setAccountingAccountDeposit(accountingAccountCouple.getAccountingAccountDeposit());
        }

        // If mails already exists, get their ids
        if (provider != null && provider.getMails() != null
                && provider.getMails().size() > 0)
            mailService.populateMailIds(provider.getMails());

        if (provider != null && provider.getAccountingMails() != null
                && provider.getAccountingMails().size() > 0)
            mailService.populateMailIds(provider.getAccountingMails());

        // If phones already exists, get their ids
        if (provider != null && provider.getPhones() != null
                && provider.getPhones().size() > 0) {
            phoneService.populateMPhoneIds(provider.getPhones());
        }

        return providerRepository.save(provider);
    }
}
