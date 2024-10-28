package com.jss.osiris.libs.batch.service;

import java.util.List;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.model.BatchSearch;
import com.jss.osiris.libs.batch.model.BatchSettings;
import com.jss.osiris.libs.batch.model.IBatchStatistics;
import com.jss.osiris.libs.batch.model.IBatchTimeStatistics;
import com.jss.osiris.libs.exception.OsirisException;

public interface BatchService {
    public void checkBatch() throws OsirisException;

    public Batch getBatch(Integer id);

    public Batch addOrUpdateBatch(Batch batch);

    public Batch declareNewBatch(String batchCode, Integer entityId) throws OsirisException;

    public List<IBatchStatistics> getBatchStatistics();

    public List<IBatchTimeStatistics> getTimeStatisticsOfBatch(BatchSettings batchSettings);

    public List<Batch> searchBatchs(BatchSearch batchSearch);

    public void purgeBatch();

    public void purgeInvoice();

}