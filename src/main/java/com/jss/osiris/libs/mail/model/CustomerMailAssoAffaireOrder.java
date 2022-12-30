package com.jss.osiris.libs.mail.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class CustomerMailAssoAffaireOrder {
    @Id
    @SequenceGenerator(name = "customer_mail_sequence", sequenceName = "customer_mail_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_mail_sequence")
    private Integer id;

    private Integer assoAffaireOrderId;

    @ManyToOne
    @JoinColumn(name = "id_customer_mail")
    @JsonIgnoreProperties(value = { "customerMailAssoAffaireOrders", "vatMails" }, allowSetters = true)
    CustomerMail customerMail;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAssoAffaireOrderId() {
        return assoAffaireOrderId;
    }

    public void setAssoAffaireOrderId(Integer assoAffaireOrderId) {
        this.assoAffaireOrderId = assoAffaireOrderId;
    }

    public CustomerMail getCustomerMail() {
        return customerMail;
    }

    public void setCustomerMail(CustomerMail customerMail) {
        this.customerMail = customerMail;
    }

}
