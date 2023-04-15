package com.jss.osiris.modules.invoicing.model.lutecia;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ComptabiliteBean {

	@JacksonXmlProperty(localName = "releve_facturation")
	@JacksonXmlElementWrapper(useWrapping = false)
	ReleveFacturationBean releveFacturations;

	public ReleveFacturationBean getReleveFacturations() {
		return releveFacturations;
	}

	public void setReleveFacturations(ReleveFacturationBean releveFacturations) {
		this.releveFacturations = releveFacturations;
	}

}