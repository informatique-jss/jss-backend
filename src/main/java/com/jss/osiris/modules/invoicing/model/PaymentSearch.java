package com.jss.osiris.modules.invoicing.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentSearch {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Float minAmount;
    private Float maxAmount;
    private String label;
    @JsonProperty("isHideAssociatedPayments")
    private boolean isHideAssociatedPayments;
    @JsonProperty("isHideCancelledPayments")
    private boolean isHideCancelledPayments;
    @JsonProperty("isHideAppoint")
    private boolean isHideAppoint;

    private Integer idPayment;

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

    public boolean isHideAssociatedPayments() {
        return isHideAssociatedPayments;
    }

    public void setHideAssociatedPayments(boolean isHideAssociatedPayments) {
        this.isHideAssociatedPayments = isHideAssociatedPayments;
    }

    public boolean isHideCancelledPayments() {
        return isHideCancelledPayments;
    }

    public void setHideCancelledPayments(boolean isHideCancelledPayments) {
        this.isHideCancelledPayments = isHideCancelledPayments;
    }

    public boolean isHideAppoint() {
        return isHideAppoint;
    }

    public void setHideAppoint(boolean isHideAppoint) {
        this.isHideAppoint = isHideAppoint;
    }

    public Integer getIdPayment() {
        return idPayment;
    }

    public void setIdPayment(Integer idPayment) {
        this.idPayment = idPayment;
    }

}
