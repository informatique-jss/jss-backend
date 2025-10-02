package com.jss.osiris.libs.batch.service.threads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.reporting.model.ReportingUpdateFrequency;
import com.jss.osiris.modules.osiris.reporting.service.ReportingWorkingTableService;

@Service
public class ReportingWorkingTablesThread implements IOsirisThread {

        @Autowired
        ReportingWorkingTableService reportingWorkingTableService;

        public String getBatchCode() {
                return Batch.COMPUTE_REPORTING_WORKING_TABLE;
        }

        @Transactional(rollbackFor = Exception.class)
        public void executeTask(Integer entityId)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException,
                        OsirisDuplicateException {
                reportingWorkingTableService.computeReportingWorkingTable(
                                entityId == 0 ? ReportingUpdateFrequency.HOURLY : ReportingUpdateFrequency.DAILY);
        }
}
