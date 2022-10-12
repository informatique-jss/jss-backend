package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.miscellaneous.model.BillingCenter;
import com.jss.osiris.modules.miscellaneous.repository.BillingCenterRepository;

@Service
public class BillingCenterServiceImpl implements BillingCenterService {

    @Autowired
    BillingCenterRepository billingCenterRepository;

    @Autowired
    MailService mailService;

    @Autowired
    PhoneService phoneService;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Override
    @Cacheable(value = "billingCenterList", key = "#root.methodName")
    public List<BillingCenter> getBillingCenters() {
        return IterableUtils.toList(billingCenterRepository.findAll());
    }

    @Override
    @Cacheable(value = "billingCenter", key = "#id")
    public BillingCenter getBillingCenter(Integer id) {
        Optional<BillingCenter> billingCenter = billingCenterRepository.findById(id);
        if (billingCenter.isPresent())
            return billingCenter.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "billingCenterList", allEntries = true),
            @CacheEvict(value = "billingCenter", key = "#billingCenter.id")
    })
    public BillingCenter addOrUpdateBillingCenter(BillingCenter billingCenter) throws Exception {
        // If mails already exists, get their ids
        if (billingCenter != null && billingCenter.getMails() != null && billingCenter.getMails().size() > 0)
            mailService.populateMailIds(billingCenter.getMails());

        // If phones already exists, get their ids
        if (billingCenter != null && billingCenter.getPhones() != null && billingCenter.getPhones().size() > 0) {
            phoneService.populateMPhoneIds(billingCenter.getPhones());
        }

        return billingCenterRepository.save(billingCenter);
    }
}
