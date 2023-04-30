package com.jss.osiris.libs.mail.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class VatMail {
    @Id
    @SequenceGenerator(name = "customer_mail_sequence", sequenceName = "customer_mail_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_mail_sequence")
    private Integer id;

    String label;
    Float total;
    Float base;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_customer_mail")
    @JsonIgnoreProperties(value = { "vatMails", "customerMailAssoAffaireOrders" }, allowSetters = true)
    CustomerMail customerMail;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Float getBase() {
        return base;
    }

    public void setBase(Float base) {
        this.base = base;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CustomerMail getCustomerMail() {
        return customerMail;
    }

    public void setCustomerMail(CustomerMail customerMail) {
        this.customerMail = customerMail;
    }
}
