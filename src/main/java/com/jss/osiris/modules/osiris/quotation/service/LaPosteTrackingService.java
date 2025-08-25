package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.laPoste.LaPosteTracking;

public interface LaPosteTrackingService {

    public List<LaPosteTracking> getLaPosteTrackingsByProvision(Provision provision);

    public void refreshAllOpenLaPosteTrackings() throws OsirisException;

    public void updateTracking(Integer entityId) throws OsirisClientMessageException, OsirisException;
}
