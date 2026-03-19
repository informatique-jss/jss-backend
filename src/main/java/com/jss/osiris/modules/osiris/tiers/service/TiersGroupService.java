package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.tiers.model.TiersGroup;

public interface TiersGroupService {
    public TiersGroup addOrUpdateTiersGroup(TiersGroup tiersGroup) throws OsirisException, OsirisDuplicateException;

    public List<TiersGroup> getTiersGroups();

    public TiersGroup getTiersGroup(Integer id);
}
