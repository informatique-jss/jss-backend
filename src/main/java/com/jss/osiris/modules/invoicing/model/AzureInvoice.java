package com.jss.osiris.modules.invoicing.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;

@Entity
@Table(indexes = {
        @Index(name = "idx_azure_invoice_manual_document_number", columnList = "id_competent_authority,invoiceId") })
public class AzureInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @IndexedField
    private Integer id;
    private Boolean isDisabled;

    @Column(length = 2500)
    private String modelUsed;
    private Float globalDocumentConfidence;

    @Column(length = 2500)
    private String customerId;

    @Column(length = 2500)
    private String reference;
    private LocalDate invoiceDate;

    @Column(length = 2500)
    @IndexedField
    private String invoiceId;
    private Float invoiceTotal;
    private Float invoicePreTaxTotal;
    private Float invoiceTaxTotal;
    private Float invoiceNonTaxableTotal;

    @Column(length = 2500)
    private String vendorTaxId;

    private Float customerIdConfidence;
    private Float referenceConfidence;
    private Float invoiceDateConfidence;
    private Float invoiceIdConfidence;
    private Float invoiceTotalConfidence;
    private Float invoicePreTaxTotalConfidence;
    private Float invoiceTaxTotalConfidence;
    private Float invoiceNonTaxableTotalConfidence;
    private Float vendorTaxIdConfidence;

    @OneToMany(mappedBy = "azureInvoice")
    @JsonIgnoreProperties(value = { "attachments", "provision", "customerOrderForInboundInvoice", "accountingRecords",
            "competentAuthority", "provider", "confrere", "invoiceItems", "azureInvoice" }, allowSetters = true)
    private List<Invoice> invoices;

    @OneToMany(mappedBy = "azureInvoice")
    @JsonIgnoreProperties(value = { "azureInvoice" }, allowSetters = true)
    private List<Attachment> attachments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_competent_authority")
    @JsonIgnoreProperties(value = { "departments", "cities", "regions", "attachments", "accountingAccountProvider",
            "accountingAccountCustomer", "accountingAccountDepositProvider" }, allowSetters = true)
    private CompetentAuthority competentAuthority;

    Boolean toCheck;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
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

    public Float getInvoicePreTaxTotal() {
        return invoicePreTaxTotal;
    }

    public void setInvoicePreTaxTotal(Float invoicePreTaxTotal) {
        this.invoicePreTaxTotal = invoicePreTaxTotal;
    }

    public Float getInvoiceTaxTotal() {
        return invoiceTaxTotal;
    }

    public void setInvoiceTaxTotal(Float invoiceTaxTotal) {
        this.invoiceTaxTotal = invoiceTaxTotal;
    }

    public Float getInvoiceNonTaxableTotal() {
        return invoiceNonTaxableTotal;
    }

    public void setInvoiceNonTaxableTotal(Float invoiceNonTaxableTotal) {
        this.invoiceNonTaxableTotal = invoiceNonTaxableTotal;
    }

    public String getVendorTaxId() {
        return vendorTaxId;
    }

    public void setVendorTaxId(String vendorTaxId) {
        this.vendorTaxId = vendorTaxId;
    }

    public Float getCustomerIdConfidence() {
        return customerIdConfidence;
    }

    public void setCustomerIdConfidence(Float customerIdConfidence) {
        this.customerIdConfidence = customerIdConfidence;
    }

    public Float getReferenceConfidence() {
        return referenceConfidence;
    }

    public void setReferenceConfidence(Float referenceConfidence) {
        this.referenceConfidence = referenceConfidence;
    }

    public Float getInvoiceDateConfidence() {
        return invoiceDateConfidence;
    }

    public void setInvoiceDateConfidence(Float invoiceDateConfidence) {
        this.invoiceDateConfidence = invoiceDateConfidence;
    }

    public Float getInvoiceIdConfidence() {
        return invoiceIdConfidence;
    }

    public void setInvoiceIdConfidence(Float invoiceIdConfidence) {
        this.invoiceIdConfidence = invoiceIdConfidence;
    }

    public Float getInvoiceTotalConfidence() {
        return invoiceTotalConfidence;
    }

    public void setInvoiceTotalConfidence(Float invoiceTotalConfidence) {
        this.invoiceTotalConfidence = invoiceTotalConfidence;
    }

    public Float getInvoicePreTaxTotalConfidence() {
        return invoicePreTaxTotalConfidence;
    }

    public void setInvoicePreTaxTotalConfidence(Float invoicePreTaxTotalConfidence) {
        this.invoicePreTaxTotalConfidence = invoicePreTaxTotalConfidence;
    }

    public Float getInvoiceTaxTotalConfidence() {
        return invoiceTaxTotalConfidence;
    }

    public void setInvoiceTaxTotalConfidence(Float invoiceTaxTotalConfidence) {
        this.invoiceTaxTotalConfidence = invoiceTaxTotalConfidence;
    }

    public Float getInvoiceNonTaxableTotalConfidence() {
        return invoiceNonTaxableTotalConfidence;
    }

    public void setInvoiceNonTaxableTotalConfidence(Float invoiceNonTaxableTotalConfidence) {
        this.invoiceNonTaxableTotalConfidence = invoiceNonTaxableTotalConfidence;
    }

    public Float getVendorTaxIdConfidence() {
        return vendorTaxIdConfidence;
    }

    public void setVendorTaxIdConfidence(Float vendorTaxIdConfidence) {
        this.vendorTaxIdConfidence = vendorTaxIdConfidence;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
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

    public CompetentAuthority getCompetentAuthority() {
        return competentAuthority;
    }

    public void setCompetentAuthority(CompetentAuthority competentAuthority) {
        this.competentAuthority = competentAuthority;
    }

    public Boolean getToCheck() {
        return toCheck;
    }

    public void setToCheck(Boolean toCheck) {
        this.toCheck = toCheck;
    }

    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

}
