package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class PmtTpInfBean {
	@JacksonXmlProperty(localName = "InstrPrty")
	String instrPrty = "";

	@JacksonXmlProperty(localName = "SvcLvl")
	SvcLvlBean svcLvlBean;

	@JacksonXmlProperty(localName = "CtgyPurp")
	CtgyPurpBean ctgyPurpBean;

	public void setInstrPrty(String instrPrty) {
		this.instrPrty = instrPrty;
	}

	public String getInstrPrty() {
		return instrPrty;
	}

	public SvcLvlBean getSvcLvlBean() {
		if (svcLvlBean == null)
			svcLvlBean = new SvcLvlBean();
		return svcLvlBean;
	}

	public void setSvcLvlBean(SvcLvlBean svcLvlBean) {
		this.svcLvlBean = svcLvlBean;
	}

	public CtgyPurpBean getCtgyPurpBean() {
		if (ctgyPurpBean == null)
			ctgyPurpBean = new CtgyPurpBean();
		return ctgyPurpBean;
	}

	public void setCtgyPurpBean(CtgyPurpBean ctgyPurpBean) {
		this.ctgyPurpBean = ctgyPurpBean;
	}

}