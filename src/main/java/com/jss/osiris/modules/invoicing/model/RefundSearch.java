package com.jss.osiris.modules.invoicing.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundSearch {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Float minAmount;
    private Float maxAmount;
    private String label;
    private Integer idRefund;

    @JsonProperty("isHideMatchedRefunds")
    private boolean isHideMatchedRefunds;

    @JsonProperty("isHideExportedRefunds")
    private boolean isHideExportedRefunds;

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

    public boolean isHideMatchedRefunds() {
        return isHideMatchedRefunds;
    }

    public void setHideMatchedRefunds(boolean isHideMatchedRefunds) {
        this.isHideMatchedRefunds = isHideMatchedRefunds;
    }

    public boolean isHideExportedRefunds() {
        return isHideExportedRefunds;
    }

    public void setHideExportedRefunds(boolean isHideExportedRefunds) {
        this.isHideExportedRefunds = isHideExportedRefunds;
    }

    public Integer getIdRefund() {
        return idRefund;
    }

    public void setIdRefund(Integer idRefund) {
        this.idRefund = idRefund;
    }

}
