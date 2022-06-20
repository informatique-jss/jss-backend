package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.CompetentAuthority;

public interface CompetentAuthorityService {
    public List<CompetentAuthority> getCompetentAuthorities();

    public CompetentAuthority getCompetentAuthority(Integer id);

    public List<CompetentAuthority> getCompetentAuthorityByDepartment(Integer departmentId, String authority);
}
