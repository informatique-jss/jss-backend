package com.jss.osiris.modules.invoicing.model;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentSearch {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
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

}
