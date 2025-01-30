package com.jss.osiris.modules.osiris.accounting.service;

import java.util.List;

import com.jss.osiris.modules.osiris.accounting.model.SageRecord;

public interface SageRecordService {
    public SageRecord addOrUpdateSageRecord(SageRecord sageRecord);

    public void deleteExistingSageRecords(List<SageRecord> sageRecords);
}
