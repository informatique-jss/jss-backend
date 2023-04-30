package com.jss.osiris.modules.invoicing.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.Debour;

@Entity
public class InfogreffeInvoice implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private String invoiceNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_competent_authority")
	@JsonIgnoreProperties(value = { "departments", "cities", "regions" }, allowSetters = true)
	private CompetentAuthority competentAuthority;
	private String productLabel;
	private LocalDateTime invoiceDateTime;
	private Float preTaxPrice;
	private Float vatPrice;
	private String customerReference;
	private String sirenAffaire;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_debour")
	private Debour debour;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public CompetentAuthority getCompetentAuthority() {
		return competentAuthority;
	}

	public void setCompetentAuthority(CompetentAuthority competentAuthority) {
		this.competentAuthority = competentAuthority;
	}

	public String getProductLabel() {
		return productLabel;
	}

	public void setProductLabel(String productLabel) {
		this.productLabel = productLabel;
	}

	public LocalDateTime getInvoiceDateTime() {
		return invoiceDateTime;
	}

	public void setInvoiceDateTime(LocalDateTime invoiceDateTime) {
		this.invoiceDateTime = invoiceDateTime;
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

	public String getCustomerReference() {
		return customerReference;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	public String getSirenAffaire() {
		return sirenAffaire;
	}

	public void setSirenAffaire(String sirenAffaire) {
		this.sirenAffaire = sirenAffaire;
	}

	public Debour getDebour() {
		return debour;
	}

	public void setDebour(Debour debour) {
		this.debour = debour;
	}

}
