package com.jss.osiris.modules.osiris.invoicing.model;

import java.math.BigDecimal;
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

    @Column(columnDefinition = "NUMERIC(15,2)", precision = 10, scale = 2)
    private BigDecimal globalDocumentConfidence;

    @Column(length = 2500)
    private String customerId;

    @Column(length = 2500)
    private String reference;
    private LocalDate invoiceDate;

    @Column(length = 2500)
    @IndexedField
    private String invoiceId;

    @Column(columnDefinition = "NUMERIC(30,2)", precision = 15, scale = 2)
    private BigDecimal invoiceTotal;
    @Column(columnDefinition = "NUMERIC(30,2)", precision = 30, scale = 2)
    private BigDecimal invoicePreTaxTotal;
    @Column(columnDefinition = "NUMERIC(15,2)", precision = 15, scale = 2)
    private BigDecimal invoiceTaxTotal;
    @Column(columnDefinition = "NUMERIC(20,2)", precision = 20, scale = 2)
    private BigDecimal invoiceNonTaxableTotal;

    @Column(length = 2500)
    private String vendorTaxId;

    @Column(columnDefinition = "NUMERIC(15,2)", precision = 15, scale = 2)
    private BigDecimal customerIdConfidence;
    @Column(columnDefinition = "NUMERIC(15,2)", precision = 15, scale = 2)
    private BigDecimal referenceConfidence;
    @Column(columnDefinition = "NUMERIC(15,2)", precision = 15, scale = 2)
    private BigDecimal invoiceDateConfidence;
    @Column(columnDefinition = "NUMERIC(15,2)", precision = 15, scale = 2)
    private BigDecimal invoiceIdConfidence;
    @Column(columnDefinition = "NUMERIC(15,2)", precision = 15, scale = 2)
    private BigDecimal invoiceTotalConfidence;
    @Column(columnDefinition = "NUMERIC(30,2)", precision = 30, scale = 2)
    private BigDecimal invoicePreTaxTotalConfidence;
    @Column(columnDefinition = "NUMERIC(30,2)", precision = 30, scale = 2)
    private BigDecimal invoiceTaxTotalConfidence;
    @Column(columnDefinition = "NUMERIC(15,2)", precision = 15, scale = 2)
    private BigDecimal invoiceNonTaxableTotalConfidence;
    @Column(columnDefinition = "NUMERIC(15,2)", precision = 15, scale = 2)
    private BigDecimal vendorTaxIdConfidence;

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

    public BigDecimal getInvoiceTotal() {
        return invoiceTotal;
    }

    public void setInvoiceTotal(BigDecimal invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }

    public BigDecimal getInvoicePreTaxTotal() {
        return invoicePreTaxTotal;
    }

    public void setInvoicePreTaxTotal(BigDecimal invoicePreTaxTotal) {
        this.invoicePreTaxTotal = invoicePreTaxTotal;
    }

    public BigDecimal getInvoiceTaxTotal() {
        return invoiceTaxTotal;
    }

    public void setInvoiceTaxTotal(BigDecimal invoiceTaxTotal) {
        this.invoiceTaxTotal = invoiceTaxTotal;
    }

    public BigDecimal getInvoiceNonTaxableTotal() {
        return invoiceNonTaxableTotal;
    }

    public void setInvoiceNonTaxableTotal(BigDecimal invoiceNonTaxableTotal) {
        this.invoiceNonTaxableTotal = invoiceNonTaxableTotal;
    }

    public String getVendorTaxId() {
        return vendorTaxId;
    }

    public void setVendorTaxId(String vendorTaxId) {
        this.vendorTaxId = vendorTaxId;
    }

    public BigDecimal getCustomerIdConfidence() {
        return customerIdConfidence;
    }

    public void setCustomerIdConfidence(BigDecimal customerIdConfidence) {
        this.customerIdConfidence = customerIdConfidence;
    }

    public BigDecimal getReferenceConfidence() {
        return referenceConfidence;
    }

    public void setReferenceConfidence(BigDecimal referenceConfidence) {
        this.referenceConfidence = referenceConfidence;
    }

    public BigDecimal getInvoiceDateConfidence() {
        return invoiceDateConfidence;
    }

    public void setInvoiceDateConfidence(BigDecimal invoiceDateConfidence) {
        this.invoiceDateConfidence = invoiceDateConfidence;
    }

    public BigDecimal getInvoiceIdConfidence() {
        return invoiceIdConfidence;
    }

    public void setInvoiceIdConfidence(BigDecimal invoiceIdConfidence) {
        this.invoiceIdConfidence = invoiceIdConfidence;
    }

    public BigDecimal getInvoiceTotalConfidence() {
        return invoiceTotalConfidence;
    }

    public void setInvoiceTotalConfidence(BigDecimal invoiceTotalConfidence) {
        this.invoiceTotalConfidence = invoiceTotalConfidence;
    }

    public BigDecimal getInvoicePreTaxTotalConfidence() {
        return invoicePreTaxTotalConfidence;
    }

    public void setInvoicePreTaxTotalConfidence(BigDecimal invoicePreTaxTotalConfidence) {
        this.invoicePreTaxTotalConfidence = invoicePreTaxTotalConfidence;
    }

    public BigDecimal getInvoiceTaxTotalConfidence() {
        return invoiceTaxTotalConfidence;
    }

    public void setInvoiceTaxTotalConfidence(BigDecimal invoiceTaxTotalConfidence) {
        this.invoiceTaxTotalConfidence = invoiceTaxTotalConfidence;
    }

    public BigDecimal getInvoiceNonTaxableTotalConfidence() {
        return invoiceNonTaxableTotalConfidence;
    }

    public void setInvoiceNonTaxableTotalConfidence(BigDecimal invoiceNonTaxableTotalConfidence) {
        this.invoiceNonTaxableTotalConfidence = invoiceNonTaxableTotalConfidence;
    }

    public BigDecimal getVendorTaxIdConfidence() {
        return vendorTaxIdConfidence;
    }

    public void setVendorTaxIdConfidence(BigDecimal vendorTaxIdConfidence) {
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

    public BigDecimal getGlobalDocumentConfidence() {
        return globalDocumentConfidence;
    }

    public void setGlobalDocumentConfidence(BigDecimal globalDocumentConfidence) {
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
