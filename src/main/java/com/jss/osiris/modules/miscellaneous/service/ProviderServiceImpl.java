package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
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
    public List<Provider> getProviders() {
        return IterableUtils.toList(providerRepository.findAll());
    }

    @Override
    public Provider getProvider(Integer id) {
        Optional<Provider> provider = providerRepository.findById(id);
        if (provider.isPresent())
            return provider.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Provider addOrUpdateProvider(Provider provider) throws OsirisException {
        // Generate accounting accounts
        if (provider.getId() == null
                || provider.getAccountingAccountProvider() == null && provider.getAccountingAccountDeposit() == null) {
            AccountingAccountTrouple accountingAccountCouple = accountingAccountService
                    .generateAccountingAccountsForEntity(provider.getLabel(), false);
            provider.setAccountingAccountProvider(accountingAccountCouple.getAccountingAccountProvider());
            provider.setAccountingAccountDeposit(accountingAccountCouple.getAccountingAccountDeposit());
        } else {
            accountingAccountService.updateAccountingAccountLabel(provider.getAccountingAccountDeposit(),
                    provider.getLabel());
            accountingAccountService.updateAccountingAccountLabel(provider.getAccountingAccountProvider(),
                    provider.getLabel());
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
            phoneService.populatePhoneIds(provider.getPhones());
        }

        return providerRepository.save(provider);
    }
}
