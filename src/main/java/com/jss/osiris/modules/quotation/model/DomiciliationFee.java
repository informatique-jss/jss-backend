package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.IId;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_domiciliation_fee_domiciliation", columnList = "id_domiciliation"),
})
public class DomiciliationFee implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "domiciliation_fee_sequence", sequenceName = "domiciliation_fee_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "domiciliation_fee_sequence")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_domiciliation")
    private Domiciliation domiciliation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_billing_type")
    private BillingType billingType;

    private Float amount;

    private LocalDate feeDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Domiciliation getDomiciliation() {
        return domiciliation;
    }

    public void setDomiciliation(Domiciliation domiciliation) {
        this.domiciliation = domiciliation;
    }

    public BillingType getBillingType() {
        return billingType;
    }

    public void setBillingType(BillingType billingType) {
        this.billingType = billingType;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public LocalDate getFeeDate() {
        return feeDate;
    }

    public void setFeeDate(LocalDate feeDate) {
        this.feeDate = feeDate;
    }

}
