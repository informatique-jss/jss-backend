package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Gift;

public interface GiftService {
    public List<Gift> getGifts();

    public Gift getGift(Integer id);
	
	 public Gift addOrUpdateGift(Gift gift);
}
