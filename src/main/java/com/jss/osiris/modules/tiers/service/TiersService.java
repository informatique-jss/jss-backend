package com.jss.osiris.modules.tiers.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.tiers.model.ITiersSearchResult;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.model.TiersSearch;

public interface TiersService {
    public Tiers getTiers(Integer id);

    public Tiers getTiersFromUser(Integer id);

    public Tiers addOrUpdateTiers(Tiers tiers) throws OsirisException, OsirisDuplicateException;

    public Tiers getTiersByIdResponsable(Integer idResponsable);

    public void reindexTiers() throws OsirisException;

    public List<Tiers> findAllTiersTypeClient() throws OsirisException;

    public List<Tiers> findAllTiersForBillingClosureReceiptSend() throws OsirisException;

    public List<Tiers> getTiers();

    public Boolean deleteTiers(Tiers tiers)
            throws OsirisClientMessageException, OsirisException, OsirisDuplicateException;

    public List<ITiersSearchResult> searchTiers(TiersSearch tiersSearch) throws OsirisException;
}
