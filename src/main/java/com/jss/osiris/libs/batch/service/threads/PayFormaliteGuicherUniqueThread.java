package com.jss.osiris.libs.batch.service.threads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.FormaliteGuichetUniqueService;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.GuichetUniquePaymentService;

@Service
public class PayFormaliteGuicherUniqueThread implements IOsirisThread {

        @Autowired
        GuichetUniquePaymentService guichetUniquePaymentService;

        @Autowired
        FormaliteGuichetUniqueService formaliteGuichetUniqueService;

        public String getBatchCode() {
                return Batch.PAY_FORMALITE_GUICHET_UNIQUE;
        }

        @Transactional(rollbackFor = Exception.class)
        public void executeTask(Integer entityId)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException,
                        OsirisDuplicateException {
                FormaliteGuichetUnique formaliteGuichetUnique = formaliteGuichetUniqueService
                                .getFormaliteGuichetUnique(entityId);
                guichetUniquePaymentService.payFormaliteGuichetUnique(formaliteGuichetUnique);
        }
}
