package com.jss.osiris.modules.invoicing.model;

import java.time.LocalDateTime;
import java.util.List;

public class InvoiceSearch {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Float minAmount;
    private Float maxAmount;
    private List<InvoiceStatus> invoiceStatus;

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

    public List<InvoiceStatus> getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(List<InvoiceStatus> invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
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

}
