package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.modules.osiris.miscellaneous.model.InformationBanner;

public interface InformationBannerService {
    public List<InformationBanner> getInformationBanners();

    public InformationBanner getInformationBanner(Integer id);

    public InformationBanner addOrUpdateInformationBanner(InformationBanner informationBanner) throws OsirisClientMessageException;

    InformationBanner getActiveInformationBanner();
}
