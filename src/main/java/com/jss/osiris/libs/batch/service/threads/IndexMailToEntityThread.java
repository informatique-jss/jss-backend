package com.jss.osiris.libs.batch.service.threads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.MailIndexationDelegate;

@Service
public class IndexMailToEntityThread implements IOsirisThread {

    @Autowired
    MailIndexationDelegate mailIndexationDelegate;

    @Override
    public String getBatchCode() {
        return Batch.INDEX_MAIL_TO_ENTITY;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void executeTask(Integer entityId)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException,
            OsirisDuplicateException {
        mailIndexationDelegate.exportMailToFile(entityId);
    }
}