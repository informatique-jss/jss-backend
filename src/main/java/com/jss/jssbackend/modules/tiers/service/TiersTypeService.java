package com.jss.jssbackend.modules.tiers.service;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.TiersType;

public interface TiersTypeService {
    public List<TiersType> getTiersTypes();

    public TiersType getTiersType(Integer id);
}
