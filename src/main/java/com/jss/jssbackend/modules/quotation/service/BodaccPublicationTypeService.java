package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import com.jss.jssbackend.modules.quotation.model.BodaccPublicationType;

public interface BodaccPublicationTypeService {
    public List<BodaccPublicationType> getBodaccPublicationTypes();

    public BodaccPublicationType getBodaccPublicationType(Integer id);
}
