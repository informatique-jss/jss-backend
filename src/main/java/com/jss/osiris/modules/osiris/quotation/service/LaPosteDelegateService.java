package com.jss.osiris.modules.osiris.quotation.service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.laPoste.LaPosteTracking;

public interface LaPosteDelegateService {

        public LaPosteTracking getLaPosteTracking(String trackingNumber)
                        throws OsirisException, OsirisClientMessageException;

}
