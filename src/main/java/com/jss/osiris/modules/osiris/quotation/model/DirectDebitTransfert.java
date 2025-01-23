package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class DirectDebitTransfert implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	@IndexedField
	private Integer id;

	@Column(nullable = false)
	@IndexedField
	private String label;

	@Column(columnDefinition = "NUMERIC(15,2)", precision = 15, scale = 2)
	@IndexedField
	private BigDecimal transfertAmount;

	@IndexedField
	private LocalDateTime transfertDateTime;

	@Column(length = 40)
	private String transfertIban;

	@Column(length = 40)
	private String transfertBic;

	private String sepaMandateReference;
	private LocalDate sepaMandateSignatureDate;

	private String customerOrderLabel;

	private Boolean isAlreadyExported;
	private Boolean isCancelled;
	private Boolean isMatched;

	@OneToMany(mappedBy = "directDebitTransfert")
	@JsonIgnoreProperties(value = { "bankTransfert", "invoiceItems", "customerOrder", "payments",
			"deposits", "accountingRecords", "customerOrderForInboundInvoice", "creditNote", "attachments",
			"azureInvoice", "azureReceipt", "invoices", "directDebitTransfert",
			"reverseCreditNote" }, allowSetters = true)
	List<Invoice> invoices;

	@OneToMany(mappedBy = "directDebitTransfert", fetch = FetchType.LAZY)
	@JsonIgnore
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

	public BigDecimal getTransfertAmount() {
		return transfertAmount;
	}

	public void setTransfertAmount(BigDecimal transfertAmount) {
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

	public String getSepaMandateReference() {
		return sepaMandateReference;
	}

	public void setSepaMandateReference(String sepaMandateReference) {
		this.sepaMandateReference = sepaMandateReference;
	}

	public LocalDate getSepaMandateSignatureDate() {
		return sepaMandateSignatureDate;
	}

	public void setSepaMandateSignatureDate(LocalDate sepaMandateSignatureDate) {
		this.sepaMandateSignatureDate = sepaMandateSignatureDate;
	}

	public String getCustomerOrderLabel() {
		return customerOrderLabel;
	}

	public void setCustomerOrderLabel(String customerOrderLabel) {
		this.customerOrderLabel = customerOrderLabel;
	}

	public Boolean getIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(Boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	public List<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
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

}
