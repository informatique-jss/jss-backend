package com.jss.osiris.libs.batch.service.threads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.FormaliteInfogreffe;
import com.jss.osiris.modules.osiris.quotation.service.infoGreffe.FormaliteInfogreffeService;

@Service
public class RefreshDetailFormaliteInfogreffeThread implements IOsirisThread {

        @Autowired
        FormaliteInfogreffeService infogreffeFormaliteService;

        public String getBatchCode() {
                return Batch.REFRESH_FORMALITE_INFOGREFFE_DETAIL;
        }

        @Transactional(rollbackFor = Exception.class)
        public void executeTask(Integer entityId)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException,
                        OsirisDuplicateException {
                FormaliteInfogreffe formaliteInfogreffe = infogreffeFormaliteService.getFormaliteInfogreffe(entityId);

                if (formaliteInfogreffe != null)
                        infogreffeFormaliteService.refreshFormaliteInfogreffeDetail(formaliteInfogreffe);
        }
}
