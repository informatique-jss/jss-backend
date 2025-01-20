package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.Formalite;

public interface FormaliteService {
    public Formalite getFormalite(Integer id);

    public Formalite addOrUpdateFormalite(Formalite formalite);

    public List<Formalite> getFormaliteForGURefresh() throws OsirisException;

    public Formalite updateFormaliteStatusToWaitingForAC(Formalite formalite);
}
