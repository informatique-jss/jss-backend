package com.jss.osiris.libs.batch.service;

import java.util.List;

import com.jss.osiris.libs.batch.model.BatchSettings;
import com.jss.osiris.libs.exception.OsirisException;

public interface BatchSettingsService {

    public BatchSettings getById(Integer id);

    public BatchSettings getByCode(String code);

    public void initializeBatchSettings() throws OsirisException;

    public List<BatchSettings> getAllBatchSettings();

    public BatchSettings addOrUpdBatchSettings(BatchSettings batchSettings);
}
