package com.jss.osiris.modules.quotation.service.guichetUnique;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.quotation.model.Formalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;

public interface FormaliteGuichetUniqueService {

    public FormaliteGuichetUnique getFormaliteGuichetUnique(Integer id);

    public FormaliteGuichetUnique refreshFormaliteGuichetUnique(FormaliteGuichetUnique formaliteGuichetUnique,
            Formalite formalite)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException;

    public FormaliteGuichetUnique addOrUpdateFormaliteGuichetUnique(FormaliteGuichetUnique formaliteGuichetUnique);
}
