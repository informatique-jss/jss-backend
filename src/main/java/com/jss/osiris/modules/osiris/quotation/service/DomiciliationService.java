package com.jss.osiris.modules.osiris.quotation.service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.quotation.model.Domiciliation;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

public interface DomiciliationService {
    public Domiciliation getDomiciliation(Integer id);

    public Provision generateDomiciliationContract(Provision provision)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException;
}
