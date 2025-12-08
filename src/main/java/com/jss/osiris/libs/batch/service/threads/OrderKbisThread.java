package com.jss.osiris.libs.batch.service.threads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.service.infoGreffe.InfogreffeKbisService;

@Service
public class OrderKbisThread implements IOsirisThread {

        @Autowired
        InfogreffeKbisService infogreffeKbisService;

        public String getBatchCode() {
                return Batch.ORDER_KBIS;
        }

        @Transactional(rollbackFor = Exception.class)
        public void executeTask(Integer entityId) throws OsirisException {
                infogreffeKbisService.orderKbis(entityId);
        }
}
