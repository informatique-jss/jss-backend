package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.osiris.tiers.model.IResponsableSearchResult;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.model.TiersSearch;

public interface ResponsableService {
    public Responsable addOrUpdateResponsable(Responsable responsable);

    public List<Responsable> getResponsables(String searchValue);

    public Responsable getResponsable(Integer id);

    public void reindexResponsable() throws OsirisException;

    public Responsable getResponsableByLoginWeb(String loginWeb);

    public Responsable getResponsableByMail(String mail);

    public List<IResponsableSearchResult> searchResponsables(TiersSearch tiersSearch) throws OsirisException;

    public Document applyParametersDocumentToQuotation(DocumentType documentType, Responsable responsable);

    /*
     * |============================================================================
     * |______________________METHODS FOR OSIRIS V2_________________________________
     * |============================================================================
     */

    public List<Responsable> getResponsablesByTiers(Tiers tiers);
}
