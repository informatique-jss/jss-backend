package com.jss.osiris.libs.batch.service.threads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.invoicing.service.AzureReceiptService;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;

@Service
public class DoOcrOnReceiptThread implements IOsirisThread {

        @Autowired
        AttachmentService attachmentService;

        @Autowired
        AzureReceiptService azureReceiptService;

        public String getBatchCode() {
                return Batch.DO_OCR_ON_RECEIPT;
        }

        @Transactional(rollbackFor = Exception.class)
        public void executeTask(Integer entityId)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException,
                        OsirisDuplicateException {
                azureReceiptService.analyseReceipt(attachmentService.getAttachment(entityId));
        }
}
