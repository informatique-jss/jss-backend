package com.jss.jssbackend.modules.clients.service;

import com.jss.jssbackend.modules.clients.model.Tiers;

public interface TiersService {
    public Tiers getTiersById(Integer id);

    public Tiers addOrUpdateTiers(Tiers tiers);
}
