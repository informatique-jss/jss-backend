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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class BankTransfert implements Serializable, IId {

	@Id
	@IndexedField
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false)
	private String label;

	private Float transfertAmount;

	private LocalDateTime transfertDateTime;

	@Column(length = 40)
	private String transfertIban;

	@Column(length = 40)
	private String transfertBic;

	@Column(columnDefinition = "TEXT")
	private String comment;

	private Boolean isAlreadyExported;

	@OneToMany(mappedBy = "bankTransfert")
	@JsonIgnoreProperties(value = { "bankTransfert", "invoiceItems", "customerOrder", "payments",
			"deposits", "accountingRecords", "customerOrderForInboundInvoice", "creditNote", "attachments",
			"azureInvoice", "azureReceipt", "invoices",
			"reverseCreditNote" }, allowSetters = true)
	List<Invoice> invoices;

	private Boolean isCancelled;
	private Boolean isSelectedForExport;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_customer_order")
	@JsonIgnoreProperties(value = { "deposits" }, allowSetters = true)
	private CustomerOrder customerOrder;

	private Boolean isMatched;

	@OneToMany(mappedBy = "bankTransfert", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "bankTransfert", "accountingRecords", "invoice", "originPayment",
			"childrenPayments", "customerOrder" }, allowSetters = true)
	private List<Payment> payments;

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

	public Float getTransfertAmount() {
		return transfertAmount;
	}

	public void setTransfertAmount(Float transfertAmount) {
		this.transfertAmount = transfertAmount;
	}

	public LocalDateTime getTransfertDateTime() {
		return transfertDateTime;
	}

	public void setTransfertDateTime(LocalDateTime transfertDateTime) {
		this.transfertDateTime = transfertDateTime;
	}

	public String getTransfertIban() {
		return transfertIban;
	}

	public void setTransfertIban(String transfertIban) {
		this.transfertIban = transfertIban;
	}

	public String getTransfertBic() {
		return transfertBic;
	}

	public void setTransfertBic(String transfertBic) {
		this.transfertBic = transfertBic;
	}

	public Boolean getIsAlreadyExported() {
		return isAlreadyExported;
	}

	public void setIsAlreadyExported(Boolean isAlreadyExported) {
		this.isAlreadyExported = isAlreadyExported;
	}

	public List<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}

	public Boolean getIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(Boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	public Boolean getIsSelectedForExport() {
		return isSelectedForExport;
	}

	public void setIsSelectedForExport(Boolean isSelectedForExport) {
		this.isSelectedForExport = isSelectedForExport;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public Boolean getIsMatched() {
		return isMatched;
	}

	public void setIsMatched(Boolean isMatched) {
		this.isMatched = isMatched;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
