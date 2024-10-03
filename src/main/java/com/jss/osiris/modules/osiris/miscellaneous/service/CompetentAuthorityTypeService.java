package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthorityType;

public interface CompetentAuthorityTypeService {
    public List<CompetentAuthorityType> getCompetentAuthorityTypes();

    public CompetentAuthorityType getCompetentAuthorityType(Integer id);

    public CompetentAuthorityType addOrUpdateCompetentAuthorityType(CompetentAuthorityType competentAuthorityType);
}
