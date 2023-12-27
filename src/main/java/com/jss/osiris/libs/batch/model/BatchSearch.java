package com.jss.osiris.libs.batch.model;

import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.libs.node.model.Node;

public class BatchSearch {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<BatchSettings> batchSettings;
    private List<BatchStatus> batchStatus;
    private List<Node> nodes;
    private Integer entityId;

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public List<BatchSettings> getBatchSettings() {
        return batchSettings;
    }

    public void setBatchSettings(List<BatchSettings> batchSettings) {
        this.batchSettings = batchSettings;
    }

    public List<BatchStatus> getBatchStatus() {
        return batchStatus;
    }

    public void setBatchStatus(List<BatchStatus> batchStatus) {
        this.batchStatus = batchStatus;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

}
