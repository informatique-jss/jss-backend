package com.jss.osiris.modules.osiris.invoicing.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.miscellaneous.model.Vat;
import com.jss.osiris.modules.osiris.quotation.model.DomiciliationFee;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

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
@Table(indexes = { @Index(name = "idx_invoice_item_invoice", columnList = "id_invoice"),
		@Index(name = "idx_invoice_item_id_provision", columnList = "id_provision"),
		@Index(name = "idx_invoice_item_origin_provider_invoice", columnList = "id_origin_provider_invoice"), })
public class InvoiceItem implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "invoice_item_sequence", sequenceName = "invoice_item_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_item_sequence")
	private Integer id;

	@Column(nullable = false, length = 1000)
	private String label;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_billing_item")
	BillingItem billingItem;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_vat")
	Vat vat;

	private Boolean isOverridePrice;

	private Boolean isGifted;

	@Column(columnDefinition = "NUMERIC", precision = 15, scale = 2)
	private BigDecimal preTaxPrice;
	@Column(columnDefinition = "NUMERIC", precision = 15, scale = 2)
	private BigDecimal preTaxPriceReinvoiced;
	@Column(columnDefinition = "NUMERIC", precision = 15, scale = 2)
	private BigDecimal vatPrice;
	@Column(columnDefinition = "NUMERIC", precision = 15, scale = 2)
	private BigDecimal discountAmount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provision")
	@JsonIgnoreProperties(value = { "invoiceItems", "assoAffaireOrder", "providerInvoices" }, allowSetters = true)
	Provision provision;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_domiciliation_fee")
	@JsonIgnoreProperties(value = { "domiciliation" }, allowSetters = true)
	DomiciliationFee domiciliationFee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_invoice")
	@JsonIgnoreProperties(value = { "invoiceItems", "accountingRecords", "customerOrder", "attachments",
			"azureInvoice", "azureReceipt", "provision", "customerOrderForInboundInvoice" }, allowSetters = true)
	Invoice invoice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_origin_provider_invoice")
	@JsonIgnoreProperties(value = { "invoiceItems", "accountingRecords", "customerOrder", "attachments",
			"azureInvoice", "azureReceipt", "provision", "customerOrderForInboundInvoice",
			"invoiceItems" }, allowSetters = true)
	Invoice originProviderInvoice;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public BillingItem getBillingItem() {
		return billingItem;
	}

	public void setBillingItem(BillingItem billingItem) {
		this.billingItem = billingItem;
	}

	public BigDecimal getPreTaxPrice() {
		return preTaxPrice;
	}

	public void setPreTaxPrice(BigDecimal preTaxPrice) {
		this.preTaxPrice = preTaxPrice;
	}

	public BigDecimal getVatPrice() {
		return vatPrice;
	}

	public void setVatPrice(BigDecimal vatPrice) {
		this.vatPrice = vatPrice;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

	public Provision getProvision() {
		return provision;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Vat getVat() {
		return vat;
	}

	public void setVat(Vat vat) {
		this.vat = vat;
	}

	public Boolean getIsOverridePrice() {
		return isOverridePrice;
	}

	public void setIsOverridePrice(Boolean isOverridePrice) {
		this.isOverridePrice = isOverridePrice;
	}

	public Boolean getIsGifted() {
		return isGifted;
	}

	public void setIsGifted(Boolean isGifted) {
		this.isGifted = isGifted;
	}

	public BigDecimal getPreTaxPriceReinvoiced() {
		return preTaxPriceReinvoiced;
	}

	public void setPreTaxPriceReinvoiced(BigDecimal preTaxPriceReinvoiced) {
		this.preTaxPriceReinvoiced = preTaxPriceReinvoiced;
	}

	public Invoice getOriginProviderInvoice() {
		return originProviderInvoice;
	}

	public void setOriginProviderInvoice(Invoice originProviderInvoice) {
		this.originProviderInvoice = originProviderInvoice;
	}

	public DomiciliationFee getDomiciliationFee() {
		return domiciliationFee;
	}

	public void setDomiciliationFee(DomiciliationFee domiciliationFee) {
		this.domiciliationFee = domiciliationFee;
	}
}
