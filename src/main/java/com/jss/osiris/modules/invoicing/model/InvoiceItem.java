package com.jss.osiris.modules.invoicing.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.quotation.model.Provision;

@Entity
@Table(indexes = { @Index(name = "idx_invoice_item_invoice", columnList = "id_invoice"),
		@Index(name = "idx_invoice_item_id_provision", columnList = "id_provision"), })
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

	private Float preTaxPrice;

	private Float preTaxPriceReinvoiced;

	private Float vatPrice;

	private Float discountAmount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provision")
	@JsonIgnoreProperties(value = { "invoiceItems", "assoAffaireOrder" }, allowSetters = true)
	Provision provision;

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

	public Float getPreTaxPrice() {
		return preTaxPrice;
	}

	public void setPreTaxPrice(Float preTaxPrice) {
		this.preTaxPrice = preTaxPrice;
	}

	public Float getVatPrice() {
		return vatPrice;
	}

	public void setVatPrice(Float vatPrice) {
		this.vatPrice = vatPrice;
	}

	public Float getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(Float discountAmount) {
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

	public Float getPreTaxPriceReinvoiced() {
		return preTaxPriceReinvoiced;
	}

	public void setPreTaxPriceReinvoiced(Float preTaxPriceReinvoiced) {
		this.preTaxPriceReinvoiced = preTaxPriceReinvoiced;
	}

	public Invoice getOriginProviderInvoice() {
		return originProviderInvoice;
	}

	public void setOriginProviderInvoice(Invoice originProviderInvoice) {
		this.originProviderInvoice = originProviderInvoice;
	}
}
