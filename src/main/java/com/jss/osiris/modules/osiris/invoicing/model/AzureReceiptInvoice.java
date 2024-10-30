package com.jss.osiris.modules.osiris.invoicing.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
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
@Table(indexes = {
        @Index(name = "idx_azure_receipt_invoice", columnList = "id_azure_receipt") })
public class AzureReceiptInvoice implements IId {

    @Id
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_azure_receipt")
    @JsonIgnoreProperties(value = { "azureReceiptInvoices" }, allowSetters = true)
    private AzureReceipt azureReceipt;

    private String invoiceId;

    @Column(columnDefinition = "NUMERIC(40,2)", precision = 40, scale = 2)
    private BigDecimal invoiceTotal;
    private Boolean isReconciliated;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public BigDecimal getInvoiceTotal() {
        return invoiceTotal;
    }

    public void setInvoiceTotal(BigDecimal invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }

    public AzureReceipt getAzureReceipt() {
        return azureReceipt;
    }

    public void setAzureReceipt(AzureReceipt azureReceipt) {
        this.azureReceipt = azureReceipt;
    }

    public Boolean getIsReconciliated() {
        return isReconciliated;
    }

    public void setIsReconciliated(Boolean isReconciliated) {
        this.isReconciliated = isReconciliated;
    }

}
