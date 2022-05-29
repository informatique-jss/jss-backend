package com.jss.jssbackend.modules.tiers.service;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.TiersFollowup;

public interface TiersFollowupService {
    public List<TiersFollowup> getTiersFollowups();

    public TiersFollowup getTiersFollowup(Integer id);

    public List<TiersFollowup> addTiersFollowup(TiersFollowup tiersFollowup);
}
