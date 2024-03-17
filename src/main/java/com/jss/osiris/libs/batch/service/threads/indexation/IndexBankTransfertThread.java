package com.jss.osiris.libs.batch.service.threads.indexation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.service.BankTransfertService;

@Service
public class IndexBankTransfertThread extends IndexThread {

    @Autowired
    BankTransfertService bankTransfertService;

    @Override
    public String getBatchCode() {
        return Batch.REINDEX_BANK_TRANSFERT;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IId getEntity(Integer entityId) {
        return bankTransfertService.getBankTransfert(entityId);
    }

}
