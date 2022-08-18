package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;

public interface CompetentAuthorityService {
    public List<CompetentAuthority> getCompetentAuthorities();

    public CompetentAuthority getCompetentAuthority(Integer id);

    public CompetentAuthority addOrUpdateCompetentAuthority(CompetentAuthority competentAuthority) throws Exception;

    public List<CompetentAuthority> getCompetentAuthorityByDepartment(Integer departmentId, String authority);

    public List<CompetentAuthority> getCompetentAuthorityByCity(Integer cityId);
}
