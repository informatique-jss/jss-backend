package com.jss.osiris.modules.osiris.accounting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.accounting.model.SageRecord;
import com.jss.osiris.modules.osiris.accounting.repository.SageRecordRepository;

@Service
public class SageRecordServiceImpl implements SageRecordService {
    @Autowired
    SageRecordRepository sageRecordRepository;

    @Override
    public SageRecord addOrUpdateSageRecord(SageRecord sageRecord) {
        return sageRecordRepository.save(sageRecord);
    }

    @Override
    public void deleteExistingSageRecords(List<SageRecord> sageRecords) {
        if (sageRecords != null && !sageRecords.isEmpty()) {
            for (SageRecord sageRecord : sageRecords) {
                List<SageRecord> foundRecords = sageRecordRepository
                        .findByTargetAccountingAccountAndOperationDate(sageRecord.getTargetAccountingAccount(),
                                sageRecord.getOperationDate());
                if (foundRecords != null && !foundRecords.isEmpty()) {
                    sageRecordRepository.deleteAll(foundRecords);
                }
            }
        }
    }
}
