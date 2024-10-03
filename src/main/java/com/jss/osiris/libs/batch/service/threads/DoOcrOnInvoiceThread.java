package com.jss.osiris.libs.batch.service.threads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.invoicing.service.AzureInvoiceService;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;

@Service
public class DoOcrOnInvoiceThread implements IOsirisThread {

        @Autowired
        AttachmentService attachmentService;

        @Autowired
        AzureInvoiceService azureInvoiceService;

        public String getBatchCode() {
                return Batch.DO_OCR_ON_INVOICE;
        }

        @Transactional(rollbackFor = Exception.class)
        public void executeTask(Integer entityId)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException,
                        OsirisDuplicateException {
                azureInvoiceService.analyseInvoice(attachmentService.getAttachment(entityId));
        }
}
