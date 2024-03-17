package com.jss.osiris.libs.batch.service.threads.indexation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.service.QuotationService;

@Service
public class IndexQuotationThread extends IndexThread {

    @Autowired
    QuotationService quotationService;

    @Override
    public String getBatchCode() {
        return Batch.REINDEX_QUOTATION;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IId getEntity(Integer entityId) {
        return quotationService.getQuotation(entityId);
    }

}
