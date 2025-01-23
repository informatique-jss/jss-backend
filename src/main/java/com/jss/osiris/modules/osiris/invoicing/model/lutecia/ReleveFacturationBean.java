package com.jss.osiris.modules.osiris.invoicing.model.lutecia;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ReleveFacturationBean {

	@JacksonXmlProperty(localName = "facture")
	@JacksonXmlElementWrapper(useWrapping = false)
	List<FactureBean> factures;

	@JacksonXmlProperty(isAttribute = true)
	private String numero;

	@JacksonXmlProperty(isAttribute = true)
	private String cle;

	@JacksonXmlProperty(isAttribute = true, localName = "compte_client")
	private String compteClient;

	public List<FactureBean> getFactures() {
		return factures;
	}

	public void setFactures(List<FactureBean> factures) {
		this.factures = factures;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getCle() {
		return cle;
	}

	public void setCle(String cle) {
		this.cle = cle;
	}

	public String getCompteClient() {
		return compteClient;
	}

	public void setCompteClient(String compteClient) {
		this.compteClient = compteClient;
	}

}