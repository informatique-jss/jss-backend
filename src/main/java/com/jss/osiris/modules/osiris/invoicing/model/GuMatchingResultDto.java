package com.jss.osiris.modules.osiris.invoicing.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GuMatchingResultDto {

    private LocalDate date; // Inpi date if missing in OSIRIS and OSIRIS date if missing in INPI

    private Integer invoiceId;

    private String liasseNumber;

    private Integer customerOrderId;

    private BigDecimal inpiAmount;

    private BigDecimal osirisAmount;

    private String matchingStatus;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getLiasseNumber() {
        return liasseNumber;
    }

    public void setLiasseNumber(String liasseNumber) {
        this.liasseNumber = liasseNumber;
    }

    public Integer getCustomerOrderId() {
        return customerOrderId;
    }

    public void setCustomerOrderId(Integer customerOrderId) {
        this.customerOrderId = customerOrderId;
    }

    public BigDecimal getInpiAmount() {
        return inpiAmount;
    }

    public void setInpiAmount(BigDecimal inpiAmount) {
        this.inpiAmount = inpiAmount;
    }

    public BigDecimal getOsirisAmount() {
        return osirisAmount;
    }

    public void setOsirisAmount(BigDecimal osirisAmount) {
        this.osirisAmount = osirisAmount;
    }

    public String getMatchingStatus() {
        return matchingStatus;
    }

    public void setMatchingStatus(String matchingStatus) {
        this.matchingStatus = matchingStatus;
    }

}
