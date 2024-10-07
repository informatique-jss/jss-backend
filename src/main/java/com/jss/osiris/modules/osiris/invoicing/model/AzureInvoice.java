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

    @Column(precision = 2)
    private Float globalDocumentConfidence;

    @Column(length = 2500)
    private String customerId;

    @Column(length = 2500)
    private String reference;
    private LocalDate invoiceDate;

    @Column(length = 2500)
    @IndexedField
    private String invoiceId;

    @Column(precision = 2)
    private Float invoiceTotal;
    @Column(precision = 2)
    private Float invoicePreTaxTotal;
    @Column(precision = 2)
    private Float invoiceTaxTotal;
    @Column(precision = 2)
    private Float invoiceNonTaxableTotal;

    @Column(length = 2500)
    private String vendorTaxId;

    @Column(precision = 2)
    private Float customerIdConfidence;
    @Column(precision = 2)
    private Float referenceConfidence;
    @Column(precision = 2)
    private Float invoiceDateConfidence;
    @Column(precision = 2)
    private Float invoiceIdConfidence;
    @Column(precision = 2)
    private Float invoiceTotalConfidence;
    @Column(precision = 2)
    private Float invoicePreTaxTotalConfidence;
    @Column(precision = 2)
    private Float invoiceTaxTotalConfidence;
    @Column(precision = 2)
    private Float invoiceNonTaxableTotalConfidence;
    @Column(precision = 2)
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
