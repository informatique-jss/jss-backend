package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class BankTransfert implements Serializable, IId {

	@Id
	@IndexedField
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false)
	@IndexedField
	private String label;

	private Float transfertAmount;

	private LocalDateTime transfertDateTime;

	@Column(length = 40)
	private String transfertIban;

	@Column(length = 40)
	private String transfertBic;

	private Boolean isAlreadyExported;

	@OneToMany(mappedBy = "bankTransfert")
	@JsonIgnoreProperties(value = { "bankTransfert", "payment", "accountingRecords", "provision",
			"invoiceItem" }, allowSetters = true)
	List<Debour> debours;

	@OneToMany(mappedBy = "bankTransfert")
	@JsonIgnoreProperties(value = { "bankTransfert", "invoiceItems", "customerOrder", "payments",
			"deposits", "accountingRecords", "customerOrderForInboundInvoice", "creditNote",
			"reverseCreditNote" }, allowSetters = true)
	List<Invoice> invoices;

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

	public String getTransfertIban() {
		return transfertIban;
	}

	public void setTransfertIban(String transfertIban) {
		this.transfertIban = transfertIban;
	}

	public List<Debour> getDebours() {
		return debours;
	}

	public void setDebours(List<Debour> debours) {
		this.debours = debours;
	}

	public List<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}

}
