package com.jss.osiris.modules.quotation.service.guichetUnique;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.Formalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;

public interface FormaliteGuichetUniqueService {

    public FormaliteGuichetUnique getFormaliteGuichetUnique(Integer id);

    public FormaliteGuichetUnique refreshFormaliteGuichetUnique(Integer id, Employee employee, Formalite formalite)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException;
}
