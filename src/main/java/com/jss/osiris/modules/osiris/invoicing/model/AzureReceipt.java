package com.jss.osiris.modules.osiris.invoicing.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class AzureReceipt {

    @Id
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    private Integer id;

    private String modelUsed;

    @Column(scale = 2)
    private Double globalDocumentConfidence;

    @OneToMany(mappedBy = "azureReceipt", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = { "azureReceipt" }, allowSetters = true)
    private List<AzureReceiptInvoice> azureReceiptInvoices;

    @OneToMany(mappedBy = "azureReceipt")
    @JsonIgnoreProperties(value = { "azureReceipt" }, allowSetters = true)
    private List<Attachment> attachments;

    private Boolean isReconciliated;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getModelUsed() {
        return modelUsed;
    }

    public void setModelUsed(String modelUsed) {
        this.modelUsed = modelUsed;
    }

    public Double getGlobalDocumentConfidence() {
        return globalDocumentConfidence;
    }

    public void setGlobalDocumentConfidence(Double globalDocumentConfidence) {
        this.globalDocumentConfidence = globalDocumentConfidence;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<AzureReceiptInvoice> getAzureReceiptInvoices() {
        return azureReceiptInvoices;
    }

    public void setAzureReceiptInvoices(List<AzureReceiptInvoice> azureReceiptInvoices) {
        this.azureReceiptInvoices = azureReceiptInvoices;
    }

    public Boolean getIsReconciliated() {
        return isReconciliated;
    }

    public void setIsReconciliated(Boolean isReconciliated) {
        this.isReconciliated = isReconciliated;
    }

}