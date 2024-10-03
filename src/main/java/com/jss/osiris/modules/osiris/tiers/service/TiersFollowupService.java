package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.tiers.model.TiersFollowup;

public interface TiersFollowupService {
    public List<TiersFollowup> getTiersFollowups();

    public TiersFollowup getTiersFollowup(Integer id);

    public List<TiersFollowup> addTiersFollowup(TiersFollowup tiersFollowup) throws OsirisException;
}
