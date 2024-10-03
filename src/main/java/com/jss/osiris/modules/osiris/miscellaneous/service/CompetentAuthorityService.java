package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.City;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthorityType;

public interface CompetentAuthorityService {
        public List<CompetentAuthority> getCompetentAuthorities();

        public CompetentAuthority getCompetentAuthority(Integer id);

        public CompetentAuthority addOrUpdateCompetentAuthority(CompetentAuthority competentAuthority)
                        throws OsirisException;

        public List<CompetentAuthority> getCompetentAuthorityByDepartment(Integer departmentId, String authority);

        public List<CompetentAuthority> getCompetentAuthorityByCity(Integer cityId);

        public CompetentAuthority getCompetentAuthorityByApiId(String apiId);

        public List<CompetentAuthority> getCompetentAuthorityByAuthorityType(Integer competentAuthorityTypeId);

        public CompetentAuthority getCompetentAuthorityByIntercommunityVat(String intercommunityVat);

        public CompetentAuthority getCompetentAuthorityByAzureCustomReference(String azureCustomReference);

        public List<CompetentAuthority> getCompetentAuthorityByInpiReference(String inpiReference);

        public List<CompetentAuthority> getCompetentAuthorityByCityAndAuthorityType(City city,
                        CompetentAuthorityType competentAuthorityType);

        public void sendRemindersToCompetentAuthorities() throws OsirisException, OsirisClientMessageException;
}
