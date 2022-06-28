package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.miscellaneous.repository.SpecialOfferRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpecialOfferServiceImpl implements SpecialOfferService {

    @Autowired
    SpecialOfferRepository specialOfferRepository;

    @Override
    public List<SpecialOffer> getSpecialOffers() {
        return IterableUtils.toList(specialOfferRepository.findAll());
    }

    @Override
    public SpecialOffer getSpecialOffer(Integer id) {
        Optional<SpecialOffer> specialOffer = specialOfferRepository.findById(id);
        if (!specialOffer.isEmpty())
            return specialOffer.get();
        return null;
    }
	
	 @Override
    public SpecialOffer addOrUpdateSpecialOffer(
            SpecialOffer specialOffer) {
        return specialOfferRepository.save(specialOffer);
    }
}
