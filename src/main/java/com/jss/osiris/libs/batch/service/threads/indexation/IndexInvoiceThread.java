package com.jss.osiris.libs.batch.service.threads.indexation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

@Service
public class IndexInvoiceThread extends IndexThread {

    @Autowired
    InvoiceService invoiceService;

    @Override
    public String getBatchCode() {
        return Batch.REINDEX_INVOICE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IId getEntity(Integer entityId) {
        return invoiceService.getInvoice(entityId);
    }

}
