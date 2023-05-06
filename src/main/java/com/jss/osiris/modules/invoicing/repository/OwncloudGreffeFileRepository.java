package com.jss.osiris.modules.invoicing.repository;

import com.jss.osiris.libs.QueryCacheCrudRepository;

import com.jss.osiris.modules.invoicing.model.OwncloudGreffeFile;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;

public interface OwncloudGreffeFileRepository extends QueryCacheCrudRepository<OwncloudGreffeFile, Integer> {

    OwncloudGreffeFile findByCompetentAuthorityAndFilename(CompetentAuthority competentAuthority, String filename);
}