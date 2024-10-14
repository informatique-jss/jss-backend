package com.jss.osiris.libs.mail.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;

public class VatMail {
    String label;
    @Column(columnDefinition = "NUMERIC", precision = 10, scale = 2)
    BigDecimal total;
    @Column(columnDefinition = "NUMERIC", precision = 10, scale = 2)
    BigDecimal base;
    CustomerMail customerMail;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getBase() {
        return base;
    }

    public void setBase(BigDecimal base) {
        this.base = base;
    }

    public CustomerMail getCustomerMail() {
        return customerMail;
    }

    public void setCustomerMail(CustomerMail customerMail) {
        this.customerMail = customerMail;
    }
}
