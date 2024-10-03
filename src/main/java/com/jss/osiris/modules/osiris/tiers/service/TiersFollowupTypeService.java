package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;

import com.jss.osiris.modules.osiris.tiers.model.TiersFollowupType;

public interface TiersFollowupTypeService {
    public List<TiersFollowupType> getTiersFollowupTypes();

    public TiersFollowupType getTiersFollowupType(Integer id);

    public TiersFollowupType addOrUpdateTiersFollowupType(TiersFollowupType tiersFollowupType);
}
