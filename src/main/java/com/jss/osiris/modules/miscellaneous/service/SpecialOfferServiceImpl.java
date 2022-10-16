package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.miscellaneous.model.AssoSpecialOfferBillingType;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.miscellaneous.repository.SpecialOfferRepository;

@Service
public class SpecialOfferServiceImpl implements SpecialOfferService {

    @Autowired
    SpecialOfferRepository specialOfferRepository;

    @Autowired
    AssoSpecialOfferBillingTypeService assoSpecialOfferBillingTypeService;

    @Override
    public List<SpecialOffer> getSpecialOffers() {
        return IterableUtils.toList(specialOfferRepository.findAll());
    }

    @Override
    public SpecialOffer getSpecialOffer(Integer id) {
        Optional<SpecialOffer> specialOffer = specialOfferRepository.findById(id);
        if (specialOffer.isPresent())
            return specialOffer.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SpecialOffer addOrUpdateSpecialOffer(
            SpecialOffer specialOffer) {

        SpecialOffer currentSpecialOffer = specialOfferRepository.save(specialOffer);

        if (currentSpecialOffer.getAssoSpecialOfferBillingTypes() != null
                && currentSpecialOffer.getAssoSpecialOfferBillingTypes().size() > 0) {
            for (AssoSpecialOfferBillingType asso : currentSpecialOffer.getAssoSpecialOfferBillingTypes()) {
                if (asso.getId() != null)
                    assoSpecialOfferBillingTypeService.deleteAssociationSpecialOfferBillingType(asso);
            }
        }

        if (specialOffer.getAssoSpecialOfferBillingTypes() != null
                && specialOffer.getAssoSpecialOfferBillingTypes().size() > 0) {
            for (AssoSpecialOfferBillingType asso : specialOffer.getAssoSpecialOfferBillingTypes()) {
                assoSpecialOfferBillingTypeService.addOrUpdateAssoSpecialOfferBillingItem(asso);
            }
        }

        return getSpecialOffer(currentSpecialOffer.getId());
    }
}
