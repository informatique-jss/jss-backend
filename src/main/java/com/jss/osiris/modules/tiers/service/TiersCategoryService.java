package com.jss.osiris.modules.tiers.service;

import java.util.List;

import com.jss.osiris.modules.tiers.model.TiersCategory;

public interface TiersCategoryService {
    public List<TiersCategory> getTiersCategories();

    public TiersCategory getTiersCategory(Integer id);
	
	 public TiersCategory addOrUpdateTiersCategory(TiersCategory tiersCategory);
}
