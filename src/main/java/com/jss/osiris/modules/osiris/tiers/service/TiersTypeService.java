package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;

import com.jss.osiris.modules.osiris.tiers.model.TiersType;

public interface TiersTypeService {
    public List<TiersType> getTiersTypes();

    public TiersType getTiersType(Integer id);

    public TiersType addOrUpdateTiersType(TiersType tiersType);
}
