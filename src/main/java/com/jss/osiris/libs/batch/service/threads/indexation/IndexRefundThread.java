package com.jss.osiris.libs.batch.service.threads.indexation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.modules.invoicing.service.RefundService;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Service
public class IndexRefundThread extends IndexThread {

    @Autowired
    RefundService refundService;

    @Override
    public String getBatchCode() {
        return Batch.REINDEX_REFUND;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IId getEntity(Integer entityId) {
        return refundService.getRefund(entityId);
    }

}
