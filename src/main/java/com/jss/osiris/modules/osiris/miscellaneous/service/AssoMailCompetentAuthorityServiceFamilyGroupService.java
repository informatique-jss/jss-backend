package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.AssoMailCompetentAuthorityServiceFamilyGroup;

public interface AssoMailCompetentAuthorityServiceFamilyGroupService {
    public List<AssoMailCompetentAuthorityServiceFamilyGroup> getAssoMailCompetentAuthorityServiceFamilyGroups();

    public AssoMailCompetentAuthorityServiceFamilyGroup getAssoMailCompetentAuthorityServiceFamilyGroup(Integer id);

    public AssoMailCompetentAuthorityServiceFamilyGroup addOrUpdateAssoMailCompetentAuthorityServiceFamilyGroup(
            AssoMailCompetentAuthorityServiceFamilyGroup assoMailCompetentAuthorityServiceFamilyGroup);
}
