package com.jss.osiris.modules.invoicing.model;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentSearch {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Float minAmount;
    private Float maxAmount;
    private String label;
    private List<PaymentWay> paymentWays;

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

    public List<PaymentWay> getPaymentWays() {
        return paymentWays;
    }

    public void setPaymentWays(List<PaymentWay> paymentWays) {
        this.paymentWays = paymentWays;
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

}
