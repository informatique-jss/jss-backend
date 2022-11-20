package com.jss.osiris.libs.mail.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class CustomerMailAssoAffaireOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_mail_sequence")
    private Integer id;

    private Integer assoAffaireOrderId;

    @ManyToOne
    @JoinColumn(name = "id_customer_mail")
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
