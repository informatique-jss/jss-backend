package com.jss.osiris.libs.batch.service.threads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.quotation.service.infoGreffe.FormaliteInfogreffeService;

@Service
public class RefreshFormaliteInfogreffeThread implements IOsirisThread {

        @Autowired
        FormaliteInfogreffeService infogreffeFormaliteService;

        public String getBatchCode() {
                return Batch.REFRESH_FORMALITE_INFOGREFFE;
        }

        @Transactional(rollbackFor = Exception.class)
        public void executeTask(Integer entityId)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException,
                        OsirisDuplicateException {
                infogreffeFormaliteService.refreshAllFormaliteInfogreffe(entityId == 1);
        }
}