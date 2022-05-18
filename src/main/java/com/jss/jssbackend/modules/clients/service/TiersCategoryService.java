package com.jss.jssbackend.modules.clients.service;

import java.util.List;

import com.jss.jssbackend.modules.clients.model.TiersCategory;

public interface TiersCategoryService {
    public List<TiersCategory> getTiersCategories();

    public TiersCategory getTiersCategory(Integer id);
}
