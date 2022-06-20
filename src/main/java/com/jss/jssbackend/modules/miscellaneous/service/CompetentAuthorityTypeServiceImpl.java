package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.miscellaneous.model.CompetentAuthorityType;
import com.jss.jssbackend.modules.miscellaneous.repository.CompetentAuthorityTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompetentAuthorityTypeServiceImpl implements CompetentAuthorityTypeService {

    @Autowired
    CompetentAuthorityTypeRepository competentAuthorityTypeRepository;

    @Override
    public List<CompetentAuthorityType> getCompetentAuthorityTypes() {
        return IterableUtils.toList(competentAuthorityTypeRepository.findAll());
    }

    @Override
    public CompetentAuthorityType getCompetentAuthorityType(Integer id) {
        Optional<CompetentAuthorityType> competentAuthorityType = competentAuthorityTypeRepository.findById(id);
        if (!competentAuthorityType.isEmpty())
            return competentAuthorityType.get();
        return null;
    }
}
