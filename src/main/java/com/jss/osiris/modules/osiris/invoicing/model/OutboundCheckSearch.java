package com.jss.osiris.modules.osiris.invoicing.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OutboundCheckSearch {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Float minAmount;
    private Float maxAmount;
    private String label;

    @JsonProperty("isDisplayNonMatchedOutboundChecks")
    private Boolean isDisplayNonMatchedOutboundChecks;

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

    public Boolean getIsDisplayNonMatchedOutboundChecks() {
        return isDisplayNonMatchedOutboundChecks;
    }

    public void setIsDisplayNonMatchedOutboundChecks(Boolean isDisplayNonMatchedOutboundChecks) {
        this.isDisplayNonMatchedOutboundChecks = isDisplayNonMatchedOutboundChecks;
    }

}
