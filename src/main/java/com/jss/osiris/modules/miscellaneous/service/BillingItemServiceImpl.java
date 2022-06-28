package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.repository.BillingItemRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (!billingItem.isEmpty())
            return billingItem.get();
        return null;
    }
	
	 @Override
    public BillingItem addOrUpdateBillingItem(
            BillingItem billingItem) {
        return billingItemRepository.save(billingItem);
    }
}
