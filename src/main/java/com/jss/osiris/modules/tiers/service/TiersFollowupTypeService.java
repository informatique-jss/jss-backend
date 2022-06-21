package com.jss.osiris.modules.tiers.service;

import java.util.List;

import com.jss.osiris.modules.tiers.model.TiersFollowupType;

public interface TiersFollowupTypeService {
    public List<TiersFollowupType> getTiersFollowupTypes();

    public TiersFollowupType getTiersFollowupType(Integer id);
}
