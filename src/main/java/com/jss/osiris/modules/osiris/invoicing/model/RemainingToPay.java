package com.jss.osiris.modules.osiris.invoicing.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RemainingToPay implements Serializable, ICreatedDate {

    private LocalDateTime createdDate;
    private BigDecimal remainingToPay;

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public BigDecimal getRemainingToPay() {
        return remainingToPay;
    }

    public void setRemainingToPay(BigDecimal remainingToPay) {
        this.remainingToPay = remainingToPay;
    }

}