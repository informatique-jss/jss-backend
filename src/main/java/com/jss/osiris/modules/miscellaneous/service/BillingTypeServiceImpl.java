package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.repository.BillingTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillingTypeServiceImpl implements BillingTypeService {

    @Autowired
    BillingTypeRepository billingTypeRepository;

    @Override
    public List<BillingType> getBillingTypes() {
        return IterableUtils.toList(billingTypeRepository.findAll());
    }

    @Override
    public BillingType getBillingType(Integer id) {
        Optional<BillingType> billingType = billingTypeRepository.findById(id);
        if (!billingType.isEmpty())
            return billingType.get();
        return null;
    }
	
	 @Override
    public BillingType addOrUpdateBillingType(
            BillingType billingType) {
        return billingTypeRepository.save(billingType);
    }
}
