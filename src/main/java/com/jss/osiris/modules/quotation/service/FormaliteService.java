package com.jss.osiris.modules.quotation.service;

import com.jss.osiris.modules.quotation.model.guichetUnique.Formalite;

public interface FormaliteService {
    public Formalite getFormalite(Integer id);

    public Formalite addOrUpdateFormalite(Formalite formalite);
}
