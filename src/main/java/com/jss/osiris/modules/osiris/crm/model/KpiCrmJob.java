package com.jss.osiris.modules.osiris.crm.model;

import java.math.BigDecimal;

public class KpiCrmJob {
    private String id;
    private JobStatus status;
    private BigDecimal result;
    private String error;

    public KpiCrmJob(JobStatus status) {
        this.status = status;
    }

    public JobStatus getStatus() {
        return status;
    }

    public BigDecimal getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}