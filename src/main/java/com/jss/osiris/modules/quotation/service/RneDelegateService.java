package com.jss.osiris.modules.quotation.service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.guichetUnique.RneCompany;

public interface RneDelegateService {

        public RneCompany getCompanyBySiren(String siren)
                        throws OsirisException, OsirisClientMessageException;

}
