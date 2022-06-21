package com.jss.osiris.modules.tiers.service;

import java.util.List;

import com.jss.osiris.modules.tiers.model.TiersFollowup;

public interface TiersFollowupService {
    public List<TiersFollowup> getTiersFollowups();

    public TiersFollowup getTiersFollowup(Integer id);

    public List<TiersFollowup> addTiersFollowup(TiersFollowup tiersFollowup);
}
