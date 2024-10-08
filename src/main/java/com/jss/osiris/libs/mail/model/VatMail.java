package com.jss.osiris.libs.mail.model;

import jakarta.persistence.Column;

public class VatMail {
    String label;
    @Column(scale = 2)
    Double total;
    @Column(scale = 2)
    Double base;
    CustomerMail customerMail;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getBase() {
        return base;
    }

    public void setBase(Double base) {
        this.base = base;
    }

    public CustomerMail getCustomerMail() {
        return customerMail;
    }

    public void setCustomerMail(CustomerMail customerMail) {
        this.customerMail = customerMail;
    }
}
