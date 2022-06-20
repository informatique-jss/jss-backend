package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.jssbackend.modules.miscellaneous.model.CompetentAuthority;
import com.jss.jssbackend.modules.miscellaneous.model.Department;
import com.jss.jssbackend.modules.miscellaneous.repository.CompetentAuthorityRepository;

@Service
public class CompetentAuthorityServiceImpl implements CompetentAuthorityService {

    @Autowired
    CompetentAuthorityRepository competentAuthorityRepository;

    @Autowired
    DepartmentService departmentService;

    @Override
    public List<CompetentAuthority> getCompetentAuthorities() {
        return IterableUtils.toList(competentAuthorityRepository.findAll());
    }

    @Override
    public CompetentAuthority getCompetentAuthority(Integer id) {
        Optional<CompetentAuthority> competentAuthority = competentAuthorityRepository.findById(id);
        if (!competentAuthority.isEmpty())
            return competentAuthority.get();
        return null;
    }

    @Override
    public List<CompetentAuthority> getCompetentAuthorityByDepartment(Integer departmentId, String authority) {
        List<CompetentAuthority> outAuthorities = new ArrayList<CompetentAuthority>();
        List<CompetentAuthority> authorities = competentAuthorityRepository.findByLabelContainingIgnoreCase(authority);
        if (departmentId != null && authorities != null && authorities.size() > 0) {
            for (CompetentAuthority a : authorities) {
                for (Department d : a.getDepartments()) {
                    if (d.getId().equals(departmentId))
                        outAuthorities.add(a);
                }
            }
        } else {
            outAuthorities = authorities;
        }
        return outAuthorities;
    }
}
