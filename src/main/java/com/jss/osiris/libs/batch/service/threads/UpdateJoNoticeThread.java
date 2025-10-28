package com.jss.osiris.libs.batch.service.threads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.modules.osiris.quotation.service.JoNoticeService;

@Service
public class UpdateJoNoticeThread implements IOsirisThread {

        @Autowired
        JoNoticeService joNoticeService;

        public String getBatchCode() {
                return Batch.UPDATE_JO_NOTICE;
        }

        @Transactional(rollbackFor = Exception.class)
        public void executeTask(Integer entityId) {
                joNoticeService.updateJoNotices();
        }
}
