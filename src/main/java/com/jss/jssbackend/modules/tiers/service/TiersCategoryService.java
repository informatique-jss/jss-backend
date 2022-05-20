package com.jss.jssbackend.modules.tiers.service;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.TiersCategory;

public interface TiersCategoryService {
    public List<TiersCategory> getTiersCategories();

    public TiersCategory getTiersCategory(Integer id);
}
