package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.miscellaneous.model.Gift;
import com.jss.osiris.modules.miscellaneous.repository.GiftRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GiftServiceImpl implements GiftService {

    @Autowired
    GiftRepository giftRepository;

    @Override
    public List<Gift> getGifts() {
        return IterableUtils.toList(giftRepository.findAll());
    }

    @Override
    public Gift getGift(Integer id) {
        Optional<Gift> gift = giftRepository.findById(id);
        if (!gift.isEmpty())
            return gift.get();
        return null;
    }
	
	 @Override
    public Gift addOrUpdateGift(
            Gift gift) {
        return giftRepository.save(gift);
    }
}
