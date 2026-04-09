package com.jss.osiris.modules.osiris.invoicing.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.jss.osiris.libs.search.model.DoNotAudit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@DoNotAudit
@IdClass(CompositeInpiInvoicingEntityKey.class)
@Table(indexes = {
		@Index(name = "idx_inpi_invoice_extract", columnList = "inpi_order,is_credit_note", unique = true) })
public class InpiInvoicingExtract implements Serializable {

	@Id
	private Integer inpiOrder;

	@Id
	private boolean isCreditNote;

	private LocalDate accountingDate;

	private LocalDate applicationDate;

	@Column(columnDefinition = "NUMERIC(15,2)", precision = 15, scale = 2)
	private BigDecimal preTaxPrice;

	private BigDecimal vatPrice;

	private String liasseNumber;

	private String denomination;

	private String clientReference;

	public Integer getInpiOrder() {
		return inpiOrder;
	}

	public void setInpiOrder(Integer inpiOrder) {
		this.inpiOrder = inpiOrder;
	}

	public boolean isCreditNote() {
		return isCreditNote;
	}

	public void setCreditNote(boolean isCreditNote) {
		this.isCreditNote = isCreditNote;
	}

	public LocalDate getAccountingDate() {
		return accountingDate;
	}

	public void setAccountingDate(LocalDate accountingDate) {
		this.accountingDate = accountingDate;
	}

	public LocalDate getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(LocalDate applicationDate) {
		this.applicationDate = applicationDate;
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

	public String getLiasseNumber() {
		return liasseNumber;
	}

	public void setLiasseNumber(String liasseNumber) {
		this.liasseNumber = liasseNumber;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public String getClientReference() {
		return clientReference;
	}

	public void setClientReference(String clientReference) {
		this.clientReference = clientReference;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	private String label;

}
