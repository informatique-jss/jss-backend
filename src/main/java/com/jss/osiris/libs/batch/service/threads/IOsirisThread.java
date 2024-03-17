package com.jss.osiris.libs.batch.service.threads;

public interface IOsirisThread {
    public void executeTask(Integer entityId) throws Exception;

    public String getBatchCode();
}
