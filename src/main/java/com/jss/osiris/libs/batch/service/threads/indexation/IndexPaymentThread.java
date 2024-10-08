package com.jss.osiris.libs.batch.service.threads.indexation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.modules.osiris.invoicing.service.PaymentService;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

@Service
public class IndexPaymentThread extends IndexThread {

    @Autowired
    PaymentService paymentService;

    @Override
    public String getBatchCode() {
        return Batch.REINDEX_PAYMENT;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IId getEntity(Integer entityId) {
        return paymentService.getPayment(entityId);
    }

}
