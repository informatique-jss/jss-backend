package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.AssoSpecialOfferBillingType;

public interface AssoSpecialOfferBillingTypeService {
    public List<AssoSpecialOfferBillingType> getAssoSpecialOfferBillingItems();

    public AssoSpecialOfferBillingType getAssoSpecialOfferBillingItem(Integer id);

    public AssoSpecialOfferBillingType addOrUpdateAssoSpecialOfferBillingItem(
            AssoSpecialOfferBillingType assoSpecialOfferBillingItem);

    public void deleteAssociationSpecialOfferBillingType(AssoSpecialOfferBillingType assoSpecialOfferBillingType);
}
