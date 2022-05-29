package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.Gift;

public interface GiftService {
    public List<Gift> getGifts();

    public Gift getGift(Integer id);
}
