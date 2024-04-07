package com.jss.osiris.modules.quotation.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;

@Entity
public class DebourDel {

    @Id
    private Integer id;

    private String comments;
    private String checkNumber;
    private Float debourAmount;
    private Float invoicedAmount;
    private LocalDateTime paymentDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_billing_type")
    private BillingType billingType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_competent_authority")
    private CompetentAuthority competentAuthority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_payment_type")
    private PaymentType paymentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_provision")
    private Provision provision;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public Float getDebourAmount() {
        return debourAmount;
    }

    public void setDebourAmount(Float debourAmount) {
        this.debourAmount = debourAmount;
    }

    public Float getInvoicedAmount() {
        return invoicedAmount;
    }

    public void setInvoicedAmount(Float invoicedAmount) {
        this.invoicedAmount = invoicedAmount;
    }

    public LocalDateTime getPaymentDateTime() {
        return paymentDateTime;
    }

    public void setPaymentDateTime(LocalDateTime paymentDateTime) {
        this.paymentDateTime = paymentDateTime;
    }

    public BillingType getBillingType() {
        return billingType;
    }

    public void setBillingType(BillingType billingType) {
        this.billingType = billingType;
    }

    public CompetentAuthority getCompetentAuthority() {
        return competentAuthority;
    }

    public void setCompetentAuthority(CompetentAuthority competentAuthority) {
        this.competentAuthority = competentAuthority;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Provision getProvision() {
        return provision;
    }

    public void setProvision(Provision provision) {
        this.provision = provision;
    }

}
