package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class DirectDebitTransfert implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@IndexedField
	private Integer id;

	@Column(nullable = false)
	private String label;

	private Float transfertAmount;

	private LocalDateTime transfertDateTime;

	@Column(length = 40)
	private String transfertIban;

	@Column(length = 40)
	private String transfertBic;

	private String sepaMandateReference;
	private LocalDate sepaMandateSignatureDate;

	private String customerOrderLabel;

	private Boolean isAlreadyExported;

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

}
