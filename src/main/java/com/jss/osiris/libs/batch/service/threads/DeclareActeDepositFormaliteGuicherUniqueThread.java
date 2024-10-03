package com.jss.osiris.libs.batch.service.threads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.quotation.model.Formalite;
import com.jss.osiris.modules.osiris.quotation.service.ActeDepositService;
import com.jss.osiris.modules.osiris.quotation.service.FormaliteService;

@Service
public class DeclareActeDepositFormaliteGuicherUniqueThread implements IOsirisThread {

        @Autowired
        FormaliteService formaliteService;

        @Autowired
        ActeDepositService acteDepositService;

        public String getBatchCode() {
                return Batch.DECLARE_NEW_ACTE_DEPOSIT_ON_GUICHET_UNIQUE;
        }

        @Transactional(rollbackFor = Exception.class)
        public void executeTask(Integer entityId)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException,
                        OsirisDuplicateException {
                Formalite formalite = formaliteService.getFormalite(entityId);
                acteDepositService.declareActeDepositOnGuichetUnique(formalite);
        }
}
