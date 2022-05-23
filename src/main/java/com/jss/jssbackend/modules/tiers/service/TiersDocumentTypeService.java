package com.jss.jssbackend.modules.tiers.service;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.TiersDocumentType;

public interface TiersDocumentTypeService {
    public List<TiersDocumentType> getTiersDocumentTypes();

    public TiersDocumentType getTiersDocumentType(Integer id);
}
