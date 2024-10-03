package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.SpecialOffer;

public interface SpecialOfferService {
    public List<SpecialOffer> getSpecialOffers();

    public SpecialOffer getSpecialOffer(Integer id);

    public SpecialOffer addOrUpdateSpecialOffer(SpecialOffer specialOffer);
}
