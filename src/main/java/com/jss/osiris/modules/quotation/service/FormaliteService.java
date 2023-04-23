package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.Formalite;

public interface FormaliteService {
    public Formalite getFormalite(Integer id);

    public Formalite addOrUpdateFormalite(Formalite formalite);

    public List<Formalite> getFormaliteForGURefresh(Employee assignedTo) throws OsirisException;
}
