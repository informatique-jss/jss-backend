package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingType;
import com.jss.osiris.modules.osiris.miscellaneous.repository.BillingItemRepository;

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
            BillingItem billingItem) {
        return billingItemRepository.save(billingItem);
    }
}
