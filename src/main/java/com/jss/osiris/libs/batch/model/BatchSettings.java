package com.jss.osiris.libs.batch.model;

import java.io.Serializable;

import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class BatchSettings implements IId, Serializable {

    @Id
    @SequenceGenerator(name = "batch_settings_sequence", sequenceName = "batch_settings_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "batch_settings_sequence")
    private Integer id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private Integer queueSize;

    @Column(nullable = false)
    private Integer fixedRate;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Integer maxAddedNumberPerIteration;

    private Boolean isOnlyOneJob;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_batch_category")
    private BatchCategory batchCategory;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }

    public Integer getFixedRate() {
        return fixedRate;
    }

    public void setFixedRate(Integer fixedRate) {
        this.fixedRate = fixedRate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getMaxAddedNumberPerIteration() {
        return maxAddedNumberPerIteration;
    }

    public void setMaxAddedNumberPerIteration(Integer maxAddedNumberPerIteration) {
        this.maxAddedNumberPerIteration = maxAddedNumberPerIteration;
    }

    public Boolean getIsOnlyOneJob() {
        return isOnlyOneJob;
    }

    public void setIsOnlyOneJob(Boolean isOnlyOneJob) {
        this.isOnlyOneJob = isOnlyOneJob;
    }

    public BatchCategory getBatchCategory() {
        return batchCategory;
    }

    public void setBatchCategory(BatchCategory batchCategory) {
        this.batchCategory = batchCategory;
    }

}
