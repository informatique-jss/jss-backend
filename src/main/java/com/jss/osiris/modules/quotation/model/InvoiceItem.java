package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.Vat;

@Entity
public class InvoiceItem implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false)
	private String label;

	@ManyToOne
	@JoinColumn(name = "id_billing_item")
	BillingItem billingItem;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account")
	AccountingAccount accountingAccount;

	@ManyToOne
	@JoinColumn(name = "id_vat")
	Vat vat;

	private Float preTaxPrice;

	private Float vatPrice;

	private Float discountAmount;

	@ManyToOne
	@JoinColumn(name = "id_quotation")
	@JsonBackReference("quotation")
	private Quotation quotation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_customer_order")
	@JsonBackReference("customerOrder")
	private CustomerOrder customerOrder;

	@ManyToOne
	@JoinColumn(name = "id_provision")
	Provision provision;

	@ManyToOne
	@JoinColumn(name = "id_invoice")
	@JsonBackReference("invoiceItems")
	Invoice invoice;

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

	public AccountingAccount getAccountingAccount() {
		return accountingAccount;
	}

	public void setAccountingAccount(AccountingAccount accountingAccount) {
		this.accountingAccount = accountingAccount;
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

	public Quotation getQuotation() {
		return quotation;
	}

	public void setQuotation(Quotation quotation) {
		this.quotation = quotation;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
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

}
