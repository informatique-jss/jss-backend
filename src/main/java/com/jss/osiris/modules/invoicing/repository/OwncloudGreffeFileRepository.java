package com.jss.osiris.modules.invoicing.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.invoicing.model.OwncloudGreffeFile;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;

public interface OwncloudGreffeFileRepository extends CrudRepository<OwncloudGreffeFile, Integer> {

    OwncloudGreffeFile findByCompetentAuthorityAndFilename(CompetentAuthority competentAuthority, String filename);
}