package com.jss.osiris.libs.batch.service;

import java.util.List;

import com.jss.osiris.libs.batch.model.BatchStatus;
import com.jss.osiris.libs.exception.OsirisException;

public interface BatchStatusService {
    public List<BatchStatus> getBatchStatus();

    public BatchStatus getBatchStatus(Integer id);

    public BatchStatus getBatchStatusByCode(String code);

    public void updateStatusReferential() throws OsirisException;

}
