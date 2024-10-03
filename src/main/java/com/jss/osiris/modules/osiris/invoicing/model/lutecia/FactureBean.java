package com.jss.osiris.modules.osiris.invoicing.model.lutecia;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties("articles")
public class FactureBean {

	@JacksonXmlProperty(isAttribute = true)
	private String numero;

	@JacksonXmlProperty(isAttribute = true)
	private String date;

	@JacksonXmlProperty(isAttribute = true, localName = "references")
	private String references;

	@JacksonXmlProperty(localName = "HT")
	private String preTaxPrice;

	@JacksonXmlProperty(localName = "TVA")
	private String vatPrice;

	@JacksonXmlProperty(localName = "TTC")
	private String totalTaxedPrice;

	@JacksonXmlProperty(localName = "NT")
	private String nonTaxablePrice;

	@JacksonXmlProperty(localName = "TOTAL")
	private String totalPrice;

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getReferences() {
		return references;
	}

	public void setReferences(String references) {
		this.references = references;
	}

	public String getPreTaxPrice() {
		return preTaxPrice;
	}

	public void setPreTaxPrice(String preTaxPrice) {
		this.preTaxPrice = preTaxPrice;
	}

	public String getVatPrice() {
		return vatPrice;
	}

	public void setVatPrice(String vatPrice) {
		this.vatPrice = vatPrice;
	}

	public String getTotalTaxedPrice() {
		return totalTaxedPrice;
	}

	public void setTotalTaxedPrice(String totalTaxedPrice) {
		this.totalTaxedPrice = totalTaxedPrice;
	}

	public String getNonTaxablePrice() {
		return nonTaxablePrice;
	}

	public void setNonTaxablePrice(String nonTaxablePrice) {
		this.nonTaxablePrice = nonTaxablePrice;
	}

	public String getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}

}