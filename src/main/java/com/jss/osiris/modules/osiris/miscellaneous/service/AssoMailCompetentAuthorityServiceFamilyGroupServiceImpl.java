package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.model.AssoMailCompetentAuthorityServiceFamilyGroup;
import com.jss.osiris.modules.osiris.miscellaneous.repository.AssoMailCompetentAuthorityServiceFamilyGroupRepository;

@Service
public class AssoMailCompetentAuthorityServiceFamilyGroupServiceImpl
        implements AssoMailCompetentAuthorityServiceFamilyGroupService {

    @Autowired
    AssoMailCompetentAuthorityServiceFamilyGroupRepository assoMailCompetentAuthorityServiceFamilyGroupRepository;

    @Override
    public List<AssoMailCompetentAuthorityServiceFamilyGroup> getAssoMailCompetentAuthorityServiceFamilyGroups() {
        return IterableUtils.toList(assoMailCompetentAuthorityServiceFamilyGroupRepository.findAll());
    }

    @Override
    public AssoMailCompetentAuthorityServiceFamilyGroup getAssoMailCompetentAuthorityServiceFamilyGroup(Integer id) {
        Optional<AssoMailCompetentAuthorityServiceFamilyGroup> assoMailCompetentAuthorityServiceFamilyGroup = assoMailCompetentAuthorityServiceFamilyGroupRepository
                .findById(id);
        if (assoMailCompetentAuthorityServiceFamilyGroup.isPresent())
            return assoMailCompetentAuthorityServiceFamilyGroup.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssoMailCompetentAuthorityServiceFamilyGroup addOrUpdateAssoMailCompetentAuthorityServiceFamilyGroup(
            AssoMailCompetentAuthorityServiceFamilyGroup assoMailCompetentAuthorityServiceFamilyGroup) {
        return assoMailCompetentAuthorityServiceFamilyGroupRepository
                .save(assoMailCompetentAuthorityServiceFamilyGroup);
    }
}
