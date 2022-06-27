package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.BodaccPublicationType;

public interface BodaccPublicationTypeService {
    public List<BodaccPublicationType> getBodaccPublicationTypes();

    public BodaccPublicationType getBodaccPublicationType(Integer id);

    public BodaccPublicationType addOrUpdateBodaccPublicationType(BodaccPublicationType bodaccPublicationType);
}
