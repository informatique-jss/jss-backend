package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.repository.BillingItemRepository;

@Service
public class BillingItemServiceImpl implements BillingItemService {

    @Autowired
    BillingItemRepository billingItemRepository;

    @Override
    public List<BillingItem> getBillingItems() {
        return IterableUtils.toList(billingItemRepository.findAll());
    }

    @Override
    public BillingItem getBillingItem(Integer id) {
        Optional<BillingItem> billingItem = billingItemRepository.findById(id);
        if (billingItem.isPresent())
            return billingItem.get();
        return null;
    }

    @Override
    public List<BillingItem> getBillingItemByBillingType(BillingType billingType) {
        return billingItemRepository.findByBillingType(billingType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillingItem addOrUpdateBillingItem(
            BillingItem billingItem) throws Exception {
        return addOrUpdateBillingItem(billingItem, false);
    }

    @Override
    public BillingItem addOrUpdateBillingItem(
            BillingItem billingItem, boolean mustBeChargeOrProduct) throws Exception {
        return this.getBillingItem(billingItem.getId());
    }
}
