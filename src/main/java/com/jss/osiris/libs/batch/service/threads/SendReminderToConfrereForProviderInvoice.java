package com.jss.osiris.libs.batch.service.threads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.quotation.service.AnnouncementService;

@Service
public class SendReminderToConfrereForProviderInvoice implements IOsirisThread {

        @Autowired
        AnnouncementService announcementService;

        public String getBatchCode() {
                return Batch.SEND_REMINDER_TO_CONFRERE_FOR_PROVIDER_INVOICE;
        }

        @Transactional(rollbackFor = Exception.class)
        public void executeTask(Integer entityId)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException,
                        OsirisDuplicateException {
                announcementService
                                .sendReminderToConfrereForProviderInvoice(
                                                announcementService.getAnnouncement(entityId));
        }
}
