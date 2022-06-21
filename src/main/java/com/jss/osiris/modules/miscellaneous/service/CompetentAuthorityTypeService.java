package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.CompetentAuthorityType;

public interface CompetentAuthorityTypeService {
    public List<CompetentAuthorityType> getCompetentAuthorityTypes();

    public CompetentAuthorityType getCompetentAuthorityType(Integer id);
}
