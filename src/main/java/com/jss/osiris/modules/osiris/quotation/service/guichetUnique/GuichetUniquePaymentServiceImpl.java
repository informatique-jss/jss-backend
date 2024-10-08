package com.jss.osiris.modules.osiris.quotation.service.guichetUnique;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.FormaliteGuichetUnique;

@Service
public class GuichetUniquePaymentServiceImpl implements GuichetUniquePaymentService {

    @Autowired
    GuichetUniqueDelegateService guichetUniqueDelegateService;

    @Autowired
    BatchService batchService;

    @Autowired
    FormaliteGuichetUniqueService formaliteGuichetUniqueService;

    @Override
    public void payFormaliteGuichetUnique(FormaliteGuichetUnique formaliteGuichetUnique)
            throws OsirisException, OsirisClientMessageException {
        guichetUniqueDelegateService.payFormaliteGuichetUnique(formaliteGuichetUnique);
        batchService.declareNewBatch(Batch.REFRESH_FORMALITE_GUICHET_UNIQUE, formaliteGuichetUnique.getId());
    }
}