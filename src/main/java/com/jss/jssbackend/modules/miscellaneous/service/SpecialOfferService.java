package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.SpecialOffer;

public interface SpecialOfferService {
    public List<SpecialOffer> getSpecialOffers();

    public SpecialOffer getSpecialOffer(Integer id);
}
