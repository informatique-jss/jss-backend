package com.jss.osiris.modules.invoicing.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DirectDebitTransfertSearch {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Float minAmount;
    private Float maxAmount;
    private String label;
    @JsonProperty("isHideMatchedDirectDebitTransfert")
    private boolean isHideMatchedDirectDebitTransfert;
    @JsonProperty("isHideExportedDirectDebitTransfert")
    private boolean isHideExportedDirectDebitTransfert;

    private Integer idDirectDebitTransfert;

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

    public Float getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Float minAmount) {
        this.minAmount = minAmount;
    }

    public Float getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Float maxAmount) {
        this.maxAmount = maxAmount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isHideExportedDirectDebitTransfert() {
        return isHideExportedDirectDebitTransfert;
    }

    public void setHideExportedDirectDebitTransfert(boolean isHideExportedDirectDebitTransfert) {
        this.isHideExportedDirectDebitTransfert = isHideExportedDirectDebitTransfert;
    }

    public Integer getIdDirectDebitTransfert() {
        return idDirectDebitTransfert;
    }

    public void setIdDirectDebitTransfert(Integer idDirectDebitTransfert) {
        this.idDirectDebitTransfert = idDirectDebitTransfert;
    }

    public boolean isHideMatchedDirectDebitTransfert() {
        return isHideMatchedDirectDebitTransfert;
    }

    public void setHideMatchedDirectDebitTransfert(boolean isHideMatchedDirectDebitTransfert) {
        this.isHideMatchedDirectDebitTransfert = isHideMatchedDirectDebitTransfert;
    }

}
