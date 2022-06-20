package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.CompetentAuthorityType;

public interface CompetentAuthorityTypeService {
    public List<CompetentAuthorityType> getCompetentAuthorityTypes();

    public CompetentAuthorityType getCompetentAuthorityType(Integer id);
}
