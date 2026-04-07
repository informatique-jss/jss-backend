package com.jss.osiris.modules.osiris.invoicing.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
		@Index(name = "idx_inpi_invoice_extract", columnList = "inpi_order,is_credit_note",unique = true) })
public class InpiInvoicingExtract implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "inpi_invoicing_extract_sequence", sequenceName = "inpi_invoicing_extract_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inpi_invoicing_extract_sequence")
	private Integer id;

	private LocalDate accountingDate;

	private Integer inpiOrder;

	private LocalDate applicationDate;

	@Column(columnDefinition = "NUMERIC(15,2)", precision = 15, scale = 2)
	private BigDecimal preTaxPrice;

	private BigDecimal vatPrice;

	@Column(nullable = false)
	private Boolean isCreditNote;

	private String liasseNumber;

	private String denomination;

	private String clientReference;

	private String label;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDate getAccountingDate() {
		return accountingDate;
	}

	public void setAccountingDate(LocalDate accountingDate) {
		this.accountingDate = accountingDate;
	}

	public Integer getInpiOrder() {
		return inpiOrder;
	}

	public void setInpiOrder(Integer inpiOrder) {
		this.inpiOrder = inpiOrder;
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

	public Boolean getIsCreditNote() {
		return isCreditNote;
	}

	public void setIsCreditNote(Boolean isCreditNote) {
		this.isCreditNote = isCreditNote;
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

}
