package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.miscellaneous.model.AssoSpecialOfferBillingType;
import com.jss.osiris.modules.miscellaneous.repository.AssoSpecialOfferBillingTypeRepository;

@Service
public class AssoSpecialOfferBillingTypeServiceImpl implements AssoSpecialOfferBillingTypeService {

    @Autowired
    AssoSpecialOfferBillingTypeRepository assoSpecialOfferBillingTypeRepository;

    @Override
    public List<AssoSpecialOfferBillingType> getAssoSpecialOfferBillingItems() {
        return IterableUtils.toList(assoSpecialOfferBillingTypeRepository.findAll());
    }

    @Override
    public AssoSpecialOfferBillingType getAssoSpecialOfferBillingItem(Integer id) {
        Optional<AssoSpecialOfferBillingType> assoSpecialOfferBillingItem = assoSpecialOfferBillingTypeRepository
                .findById(id);
        if (assoSpecialOfferBillingItem.isPresent())
            return assoSpecialOfferBillingItem.get();
        return null;
    }

    @Override
    public AssoSpecialOfferBillingType addOrUpdateAssoSpecialOfferBillingItem(
            AssoSpecialOfferBillingType assoSpecialOfferBillingItem) {
        return assoSpecialOfferBillingTypeRepository.save(assoSpecialOfferBillingItem);
    }

    @Override
    public void deleteAssociationSpecialOfferBillingType(AssoSpecialOfferBillingType assoSpecialOfferBillingType) {
        assoSpecialOfferBillingTypeRepository.delete(assoSpecialOfferBillingType);
    }
}
