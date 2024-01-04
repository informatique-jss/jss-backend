package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.OfferReason;

public interface OfferReasonService {

    public List<OfferReason> getOfferReasons();

    public OfferReason getOfferReason(Integer id);

    public OfferReason addOrUpdateCustomerOrderOfferReason(OfferReason offerReason, String id_commande);

    public OfferReason addOrUpdateOfferReason(OfferReason offerReason);
}