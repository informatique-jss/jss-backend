package com.jss.osiris.libs.batch.service.threads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;

@Service
public class ComputeKpiCrmThread implements IOsirisThread {

    @Autowired
    KpiCrmService kpiCrmService;

    public String getBatchCode() {
        return Batch.COMPUTE_KPI_CRM;
    }

    @Transactional(rollbackFor = Exception.class)
    public void executeTask(Integer entityId)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException,
            OsirisDuplicateException {
        kpiCrmService.computeKpiCrm(entityId);
    }
}