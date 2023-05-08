package com.jss.osiris.modules.invoicing.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.Attachment;

@Entity
public class AzureReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String modelUsed;
    private Float globalDocumentConfidence;

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

    public Float getGlobalDocumentConfidence() {
        return globalDocumentConfidence;
    }

    public void setGlobalDocumentConfidence(Float globalDocumentConfidence) {
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