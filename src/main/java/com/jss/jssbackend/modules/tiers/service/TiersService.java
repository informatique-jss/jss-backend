package com.jss.jssbackend.modules.tiers.service;

import com.jss.jssbackend.modules.tiers.model.Tiers;

public interface TiersService {
    public Tiers getTiersById(Integer id);

    public Tiers addOrUpdateTiers(Tiers tiers);

    public Tiers getTiersByIdResponsable(Integer idResponsable);
}
