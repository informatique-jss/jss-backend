package com.jss.osiris.modules.tiers.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.tiers.model.IResponsableSearchResult;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.TiersSearch;

public interface ResponsableService {
    public Responsable addOrUpdateResponsable(Responsable responsable);

    public List<Responsable> getResponsables();

    public Responsable getResponsable(Integer id);

    public void reindexResponsable();

    public Responsable getResponsableByLoginWeb(String loginWeb);

    public List<IResponsableSearchResult> searchResponsables(TiersSearch tiersSearch) throws OsirisException;
}
