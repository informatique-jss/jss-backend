package com.jss.osiris.libs.batch.service.threads.indexation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.service.threads.IOsirisThread;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

@Service
public abstract class IndexThread implements IOsirisThread {

        @Autowired
        IndexEntityService indexEntityService;

        public abstract String getBatchCode();

        public abstract IId getEntity(Integer entityId);

        @Transactional(rollbackFor = Exception.class)
        public void executeTask(Integer entityId)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException,
                        OsirisDuplicateException {
                IId entity = getEntity(entityId);
                if (entity != null)
                        indexEntityService.indexEntity(entity);
        }
}
