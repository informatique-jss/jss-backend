package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthorityType;
import com.jss.osiris.modules.osiris.miscellaneous.repository.CompetentAuthorityTypeRepository;

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
        if (competentAuthorityType.isPresent())
            return competentAuthorityType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompetentAuthorityType addOrUpdateCompetentAuthorityType(
            CompetentAuthorityType competentAuthorityType) {
        return competentAuthorityTypeRepository.save(competentAuthorityType);
    }
}
