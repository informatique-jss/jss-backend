package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Gift;

public interface GiftService {
    public List<Gift> getGifts();

    public Gift getGift(Integer id);

    public Gift addOrUpdateGift(Gift gift) throws OsirisException;

    public void decreaseStock(Gift gift, Integer giftNumber) throws OsirisException;
}
