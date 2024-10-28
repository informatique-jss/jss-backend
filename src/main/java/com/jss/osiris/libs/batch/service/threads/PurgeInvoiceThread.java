package com.jss.osiris.libs.batch.service.threads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;

@Service
public class PurgeInvoiceThread implements IOsirisThread {
    @Autowired
    BatchService batchService;

    public String getBatchCode() {
        return Batch.PURGE_INVOICE;
    }

    @Transactional(rollbackFor = Exception.class)
    public void executeTask(Integer entityId)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException,
            OsirisDuplicateException {
        batchService.purgeInvoice();
    }
}
