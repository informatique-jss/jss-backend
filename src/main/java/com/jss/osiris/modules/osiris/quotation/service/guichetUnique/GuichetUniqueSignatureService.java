package com.jss.osiris.modules.osiris.quotation.service.guichetUnique;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;

public interface GuichetUniqueSignatureService {
    public void signFormalitesGuichetUnique() throws OsirisException, OsirisClientMessageException;
}
