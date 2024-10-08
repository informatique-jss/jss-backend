package com.jss.osiris.modules.osiris.invoicing.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;

import jakarta.persistence.Column;
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
@Table(indexes = {
        @Index(name = "idx_azure_invoice_manual_document_number", columnList = "id_competent_authority,invoiceId") })
public class AzureInvoice {

    @Id
    @SequenceGenerator(name = "azure_invoice_sequence", sequenceName = "azure_invoice_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "azure_invoice_sequence")
    @IndexedField
    private Integer id;
    private Boolean isDisabled;

    @Column(length = 2500)
    private String modelUsed;

    @Column(scale = 2)
    private Double globalDocumentConfidence;

    @Column(length = 2500)
    private String customerId;

    @Column(length = 2500)
    private String reference;
    private LocalDate invoiceDate;

    @Column(length = 2500)
    @IndexedField
    private String invoiceId;

    @Column(scale = 2)
    private Double invoiceTotal;
    @Column(scale = 2)
    private Double invoicePreTaxTotal;
    @Column(scale = 2)
    private Double invoiceTaxTotal;
    @Column(scale = 2)
    private Double invoiceNonTaxableTotal;

    @Column(length = 2500)
    private String vendorTaxId;

    @Column(scale = 2)
    private Double customerIdConfidence;
    @Column(scale = 2)
    private Double referenceConfidence;
    @Column(scale = 2)
    private Double invoiceDateConfidence;
    @Column(scale = 2)
    private Double invoiceIdConfidence;
    @Column(scale = 2)
    private Double invoiceTotalConfidence;
    @Column(scale = 2)
    private Double invoicePreTaxTotalConfidence;
    @Column(scale = 2)
    private Double invoiceTaxTotalConfidence;
    @Column(scale = 2)
    private Double invoiceNonTaxableTotalConfidence;
    @Column(scale = 2)
    private Double vendorTaxIdConfidence;

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

    public Double getInvoiceTotal() {
        return invoiceTotal;
    }

    public void setInvoiceTotal(Double invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }

    public Double getInvoicePreTaxTotal() {
        return invoicePreTaxTotal;
    }

    public void setInvoicePreTaxTotal(Double invoicePreTaxTotal) {
        this.invoicePreTaxTotal = invoicePreTaxTotal;
    }

    public Double getInvoiceTaxTotal() {
        return invoiceTaxTotal;
    }

    public void setInvoiceTaxTotal(Double invoiceTaxTotal) {
        this.invoiceTaxTotal = invoiceTaxTotal;
    }

    public Double getInvoiceNonTaxableTotal() {
        return invoiceNonTaxableTotal;
    }

    public void setInvoiceNonTaxableTotal(Double invoiceNonTaxableTotal) {
        this.invoiceNonTaxableTotal = invoiceNonTaxableTotal;
    }

    public String getVendorTaxId() {
        return vendorTaxId;
    }

    public void setVendorTaxId(String vendorTaxId) {
        this.vendorTaxId = vendorTaxId;
    }

    public Double getCustomerIdConfidence() {
        return customerIdConfidence;
    }

    public void setCustomerIdConfidence(Double customerIdConfidence) {
        this.customerIdConfidence = customerIdConfidence;
    }

    public Double getReferenceConfidence() {
        return referenceConfidence;
    }

    public void setReferenceConfidence(Double referenceConfidence) {
        this.referenceConfidence = referenceConfidence;
    }

    public Double getInvoiceDateConfidence() {
        return invoiceDateConfidence;
    }

    public void setInvoiceDateConfidence(Double invoiceDateConfidence) {
        this.invoiceDateConfidence = invoiceDateConfidence;
    }

    public Double getInvoiceIdConfidence() {
        return invoiceIdConfidence;
    }

    public void setInvoiceIdConfidence(Double invoiceIdConfidence) {
        this.invoiceIdConfidence = invoiceIdConfidence;
    }

    public Double getInvoiceTotalConfidence() {
        return invoiceTotalConfidence;
    }

    public void setInvoiceTotalConfidence(Double invoiceTotalConfidence) {
        this.invoiceTotalConfidence = invoiceTotalConfidence;
    }

    public Double getInvoicePreTaxTotalConfidence() {
        return invoicePreTaxTotalConfidence;
    }

    public void setInvoicePreTaxTotalConfidence(Double invoicePreTaxTotalConfidence) {
        this.invoicePreTaxTotalConfidence = invoicePreTaxTotalConfidence;
    }

    public Double getInvoiceTaxTotalConfidence() {
        return invoiceTaxTotalConfidence;
    }

    public void setInvoiceTaxTotalConfidence(Double invoiceTaxTotalConfidence) {
        this.invoiceTaxTotalConfidence = invoiceTaxTotalConfidence;
    }

    public Double getInvoiceNonTaxableTotalConfidence() {
        return invoiceNonTaxableTotalConfidence;
    }

    public void setInvoiceNonTaxableTotalConfidence(Double invoiceNonTaxableTotalConfidence) {
        this.invoiceNonTaxableTotalConfidence = invoiceNonTaxableTotalConfidence;
    }

    public Double getVendorTaxIdConfidence() {
        return vendorTaxIdConfidence;
    }

    public void setVendorTaxIdConfidence(Double vendorTaxIdConfidence) {
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

    public Double getGlobalDocumentConfidence() {
        return globalDocumentConfidence;
    }

    public void setGlobalDocumentConfidence(Double globalDocumentConfidence) {
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
