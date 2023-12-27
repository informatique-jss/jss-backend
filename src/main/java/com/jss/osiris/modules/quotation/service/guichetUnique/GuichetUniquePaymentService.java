package com.jss.osiris.modules.quotation.service.guichetUnique;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;

public interface GuichetUniquePaymentService {
    public void payFormaliteGuichetUnique(FormaliteGuichetUnique formaliteGuichetUnique)
            throws OsirisException, OsirisClientMessageException;
}
