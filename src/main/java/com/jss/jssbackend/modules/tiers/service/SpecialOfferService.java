package com.jss.jssbackend.modules.tiers.service;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.SpecialOffer;

public interface SpecialOfferService {
    public List<SpecialOffer> getSpecialOffers();

    public SpecialOffer getSpecialOffer(Integer id);
}
