package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateTimeSerializer;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Entity
public class Invoice implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false)
	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	private LocalDateTime createdDate;

	@OneToMany(targetEntity = InvoiceItem.class, mappedBy = "invoice")
	@JsonManagedReference("invoice")
	private List<InvoiceItem> invoiceItems;

	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private CustomerOrder customerOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_responsable")
	private Responsable responsable;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_confrere")
	private Confrere confrere;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tiers")
	@JsonBackReference("tiers")
	private Tiers tiers;

	@Column(length = 40)
	private String billingLabel;
	private String billingLabelAddress;
	private String billingLabelPostalCode;
	private City billingLabelCity;
	private Country billingLabelCountry;
	private Boolean billingLabelIsIndividual;

	@ManyToOne
	@JoinColumn(name = "id_billing_label_type")
	private BillingLabelType billingLabelType;

	private Boolean isResponsableOnBilling;
	private Boolean isCommandNumberMandatory;

	@Column(length = 40)
	private String commandNumber;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public List<InvoiceItem> getInvoiceItems() {
		return invoiceItems;
	}

	public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
		this.invoiceItems = invoiceItems;
	}

	public Tiers getTiers() {
		return tiers;
	}

	public void setTiers(Tiers tiers) {
		this.tiers = tiers;
	}

	public Responsable getResponsable() {
		return responsable;
	}

	public void setResponsable(Responsable responsable) {
		this.responsable = responsable;
	}

	public Confrere getConfrere() {
		return confrere;
	}

	public void setConfrere(Confrere confrere) {
		this.confrere = confrere;
	}

	public String getBillingLabel() {
		return billingLabel;
	}

	public void setBillingLabel(String billingLabel) {
		this.billingLabel = billingLabel;
	}

	public String getBillingLabelAddress() {
		return billingLabelAddress;
	}

	public void setBillingLabelAddress(String billingLabelAddress) {
		this.billingLabelAddress = billingLabelAddress;
	}

	public String getBillingLabelPostalCode() {
		return billingLabelPostalCode;
	}

	public void setBillingLabelPostalCode(String billingLabelPostalCode) {
		this.billingLabelPostalCode = billingLabelPostalCode;
	}

	public City getBillingLabelCity() {
		return billingLabelCity;
	}

	public void setBillingLabelCity(City billingLabelCity) {
		this.billingLabelCity = billingLabelCity;
	}

	public Country getBillingLabelCountry() {
		return billingLabelCountry;
	}

	public void setBillingLabelCountry(Country billingLabelCountry) {
		this.billingLabelCountry = billingLabelCountry;
	}

	public Boolean getBillingLabelIsIndividual() {
		return billingLabelIsIndividual;
	}

	public void setBillingLabelIsIndividual(Boolean billingLabelIsIndividual) {
		this.billingLabelIsIndividual = billingLabelIsIndividual;
	}

	public BillingLabelType getBillingLabelType() {
		return billingLabelType;
	}

	public void setBillingLabelType(BillingLabelType billingLabelType) {
		this.billingLabelType = billingLabelType;
	}

	public Boolean getIsResponsableOnBilling() {
		return isResponsableOnBilling;
	}

	public void setIsResponsableOnBilling(Boolean isResponsableOnBilling) {
		this.isResponsableOnBilling = isResponsableOnBilling;
	}

	public Boolean getIsCommandNumberMandatory() {
		return isCommandNumberMandatory;
	}

	public void setIsCommandNumberMandatory(Boolean isCommandNumberMandatory) {
		this.isCommandNumberMandatory = isCommandNumberMandatory;
	}

	public String getCommandNumber() {
		return commandNumber;
	}

	public void setCommandNumber(String commandNumber) {
		this.commandNumber = commandNumber;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

}
