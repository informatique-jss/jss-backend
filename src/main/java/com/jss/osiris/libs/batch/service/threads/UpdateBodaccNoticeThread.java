package com.jss.osiris.libs.batch.service.threads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.modules.osiris.quotation.service.BodaccNoticeService;

@Service
public class UpdateBodaccNoticeThread implements IOsirisThread {

        @Autowired
        BodaccNoticeService bodaccNoticeService;

        public String getBatchCode() {
                return Batch.UPDATE_BODACC_NOTICE;
        }

        @Transactional(rollbackFor = Exception.class)
        public void executeTask(Integer entityId) {
                bodaccNoticeService.updateBodacNotices();
        }
}
