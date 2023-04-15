package com.jss.osiris.modules.invoicing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.invoicing.model.OwncloudGreffeFile;
import com.jss.osiris.modules.invoicing.repository.OwncloudGreffeFileRepository;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;

@Service
public class OwncloudGreffeFileServiceImpl implements OwncloudGreffeFileService {

    @Autowired
    OwncloudGreffeFileRepository owncloudGreffeFileRepository;

    @Override
    public OwncloudGreffeFile getOwncloudGreffeFileByCompetentAuthorityAndFilename(
            CompetentAuthority competentAuthority, String filename) {
        return owncloudGreffeFileRepository.findByCompetentAuthorityAndFilename(competentAuthority, filename);
    }

    @Override
    public OwncloudGreffeFile addOrUpdateOwncloudGreffeFile(OwncloudGreffeFile owncloudGreffeFile) {
        return owncloudGreffeFileRepository.save(owncloudGreffeFile);
    }
}
