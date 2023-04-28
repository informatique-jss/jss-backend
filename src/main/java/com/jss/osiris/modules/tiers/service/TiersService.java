package com.jss.osiris.modules.tiers.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.tiers.model.Tiers;

public interface TiersService {
    public Tiers getTiers(Integer id);

    public Tiers getTiersFromUser(Integer id);

    public Tiers addOrUpdateTiers(Tiers tiers) throws OsirisException;

    public Tiers getTiersByIdResponsable(Integer idResponsable);

    public void reindexTiers();

    public List<Tiers> findAllTiersTypeClient() throws OsirisException;

    public List<Tiers> findAllTiersForBillingClosureReceiptSend() throws OsirisException;

    public List<Tiers> getTiers();
}
