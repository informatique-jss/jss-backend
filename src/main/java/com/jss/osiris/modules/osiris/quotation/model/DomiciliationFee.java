package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingType;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    @JsonIgnoreProperties(value = { "domiciliationFees" }, allowSetters = true)
    @JoinColumn(name = "id_domiciliation")
    private Domiciliation domiciliation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_billing_type")
    private BillingType billingType;

    private Float amount;

    private LocalDate feeDate;

    @OneToMany(mappedBy = "domiciliationFee")
    @JsonIgnoreProperties(value = { "domiciliationFee" }, allowSetters = true)
    private List<InvoiceItem> invoiceItems;

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

    public List<InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

}
