package com.jss.osiris.modules.invoicing.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
@Table(indexes = {
        @Index(name = "idx_azure_receipt_invoice", columnList = "id_azure_receipt") })
public class AzureReceiptInvoice implements IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_azure_receipt")
    @JsonIgnoreProperties(value = { "azureReceiptInvoices" }, allowSetters = true)
    private AzureReceipt azureReceipt;

    private String invoiceId;
    private Float invoiceTotal;
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

    public Float getInvoiceTotal() {
        return invoiceTotal;
    }

    public void setInvoiceTotal(Float invoiceTotal) {
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
