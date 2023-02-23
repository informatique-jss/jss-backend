package com.jss.osiris.modules.invoicing.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.Provision;

@Entity
public class InvoiceItem implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "invoice_item_sequence", sequenceName = "invoice_item_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_item_sequence")
	private Integer id;

	@Column(nullable = false, length = 1000)
	private String label;

	@ManyToOne
	@JoinColumn(name = "id_billing_item")
	BillingItem billingItem;

	@ManyToOne
	@JoinColumn(name = "id_vat")
	Vat vat;

	private Boolean isOverridePrice;

	private Boolean isGifted;

	private Float preTaxPrice;

	private Float vatPrice;

	private Float discountAmount;

	@ManyToOne
	@JoinColumn(name = "id_provision")
	@JsonIgnoreProperties(value = { "invoiceItems" }, allowSetters = true)
	Provision provision;

	@ManyToOne
	@JoinColumn(name = "id_invoice")
	@JsonIgnoreProperties(value = { "invoiceItems", "accountingRecords", "customerOrder",
			"customerOrderForInboundInvoice" }, allowSetters = true)
	Invoice invoice;

	@OneToMany(mappedBy = "invoiceItem")
	@JsonIgnoreProperties(value = { "invoiceItem", "payment", "accountingRecords", "provision" }, allowSetters = true)
	List<Debour> debours;

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

	public List<Debour> getDebours() {
		return debours;
	}

	public void setDebours(List<Debour> debours) {
		this.debours = debours;
	}

}
