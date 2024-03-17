package com.jss.osiris.libs.batch.model;

public interface IBatchStatistics {
    Integer getIdBatchSettings();

    Integer getNew();

    Integer getSuccess();

    Integer getWaiting();

    Integer getRunning();

    Integer getError();

    Integer getAcknowledge();

    Float getStandardMeanTime();

    Float getCurrentMeanTime();
}