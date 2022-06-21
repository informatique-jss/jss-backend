package com.jss.osiris.modules.tiers.service;

import java.util.List;

import com.jss.osiris.modules.tiers.model.Tiers;

public interface TiersService {
    public Tiers getTiers(Integer id);

    public Tiers addOrUpdateTiers(Tiers tiers);

    public Tiers getTiersByIdResponsable(Integer idResponsable);

    public List<Tiers> getIndividualTiersByKeyword(String searchedValue);

}
