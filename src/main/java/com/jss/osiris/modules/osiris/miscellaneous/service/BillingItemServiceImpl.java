package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.time.LocalDate;
import java.util.Comparator;
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
    public BillingItem getCurrentBillingItemByBillingType(BillingType billingType) {
        List<BillingItem> billingItems = getBillingItemByBillingType(billingType);

        if (billingItems != null && billingItems.size() > 0) {
            Optional<BillingItem> currentBillingItem = billingItems.stream()
                    .filter(item -> item.getStartDate() != null)
                    .filter(item -> !item.getStartDate().isAfter(LocalDate.now()))
                    .max(Comparator.comparing(BillingItem::getStartDate));
            if (currentBillingItem.isPresent())
                return currentBillingItem.get();
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillingItem addOrUpdateBillingItem(
            BillingItem billingItem) {
        return billingItemRepository.save(billingItem);
    }
}
