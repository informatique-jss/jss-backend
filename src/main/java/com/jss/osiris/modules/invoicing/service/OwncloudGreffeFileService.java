package com.jss.osiris.modules.invoicing.service;

import com.jss.osiris.modules.invoicing.model.OwncloudGreffeFile;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;

public interface OwncloudGreffeFileService {
    public OwncloudGreffeFile getOwncloudGreffeFileByCompetentAuthorityAndFilename(
            CompetentAuthority competentAuthority, String filename);

    public OwncloudGreffeFile addOrUpdateOwncloudGreffeFile(OwncloudGreffeFile owncloudGreffeFile);
}
