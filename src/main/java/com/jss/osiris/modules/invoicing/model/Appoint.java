package com.jss.osiris.modules.invoicing.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class Appoint implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String label;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_origin_payment")
	@JsonIgnoreProperties(value = { "invoice", "accountingRecords", "debours", "originPayment" }, allowSetters = true)
	private Payment originPayment;

	private Float appointAmount;

	private LocalDate appointDate;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "appoint")
	@JsonIgnoreProperties(value = { "appoint" }, allowSetters = true)
	@JsonIgnore
	List<AccountingRecord> accountingRecords;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "appoint")
	@JsonIgnoreProperties(value = { "appoint" }, allowSetters = true)
	List<Refund> refunds;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_invoice")
	@JsonIgnoreProperties(value = { "accountingRecords", "payments", "deposits", "customerOrder",
			"invoiceItems", "responsable", "tiers", "confrere", "appoints" }, allowSetters = true)
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

	public Payment getOriginPayment() {
		return originPayment;
	}

	public void setOriginPayment(Payment originPayment) {
		this.originPayment = originPayment;
	}

	public Float getAppointAmount() {
		return appointAmount;
	}

	public void setAppointAmount(Float appointAmount) {
		this.appointAmount = appointAmount;
	}

	public List<AccountingRecord> getAccountingRecords() {
		return accountingRecords;
	}

	public void setAccountingRecords(List<AccountingRecord> accountingRecords) {
		this.accountingRecords = accountingRecords;
	}

	public List<Refund> getRefunds() {
		return refunds;
	}

	public void setRefunds(List<Refund> refunds) {
		this.refunds = refunds;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public LocalDate getAppointDate() {
		return appointDate;
	}

	public void setAppointDate(LocalDate appointDate) {
		this.appointDate = appointDate;
	}

}
