package com.jss.osiris.modules.osiris.invoicing.model.lutecia;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "lutecia")
public class LuteciaBean {

	@JacksonXmlProperty(isAttribute = true)
	private String xmlns;

	@JacksonXmlProperty(localName = "comptabilite")
	ComptabiliteBean comptabiliteBean;

	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	public ComptabiliteBean getComptabiliteBean() {
		return comptabiliteBean;
	}

	public void setComptabiliteBean(ComptabiliteBean comptabiliteBean) {
		this.comptabiliteBean = comptabiliteBean;
	}

}