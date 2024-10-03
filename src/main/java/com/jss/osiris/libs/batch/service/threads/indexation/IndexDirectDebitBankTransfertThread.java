package com.jss.osiris.libs.batch.service.threads.indexation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.service.DirectDebitTransfertService;

@Service
public class IndexDirectDebitBankTransfertThread extends IndexThread {

    @Autowired
    DirectDebitTransfertService directDebitTransfertService;

    @Override
    public String getBatchCode() {
        return Batch.REINDEX_DIRECT_DEBIT_BANK_TRANSFERT;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IId getEntity(Integer entityId) {
        return directDebitTransfertService.getDirectDebitTransfert(entityId);
    }

}
