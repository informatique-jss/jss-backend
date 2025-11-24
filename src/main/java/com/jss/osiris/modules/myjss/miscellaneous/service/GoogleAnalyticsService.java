package com.jss.osiris.modules.myjss.miscellaneous.service;

import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;

@Service
public interface GoogleAnalyticsService {

    public void trackPurchase(IQuotation quotation, boolean isValidation, String gaClientId) throws OsirisException;
}
