package com.jss.jssbackend.modules.clients.service;

import java.util.List;

import com.jss.jssbackend.modules.clients.model.TiersType;

public interface TiersTypesService {
    public List<TiersType> getTiersTypes();

    public TiersType getTiersType(Integer id);
}
