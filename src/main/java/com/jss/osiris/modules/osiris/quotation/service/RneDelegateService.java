package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.RneCompany;

public interface RneDelegateService {

        public List<RneCompany> getCompanyBySiren(String siren)
                        throws OsirisException, OsirisClientMessageException;

        public List<RneCompany> getCompanyBySiret(String siren)
                        throws OsirisException, OsirisClientMessageException;

}
