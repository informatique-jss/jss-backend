package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class PmtTpInfBean {
	@JacksonXmlProperty(localName = "InstrPrty")
	String instrPrty = "";

	@JacksonXmlProperty(localName = "SvcLvl")
	SvcLvlBean svcLvlBean;

	@JacksonXmlProperty(localName = "CtgyPurp")
	CtgyPurpBean ctgyPurpBean;

	@JacksonXmlProperty(localName = "LclInstrm")
	CtgyPurpBean lclInstrmBean;

	@JacksonXmlProperty(localName = "SeqTp")
	String seqTp = "";

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

	public CtgyPurpBean getLclInstrmBean() {
		return lclInstrmBean;
	}

	public void setLclInstrmBean(CtgyPurpBean lclInstrmBean) {
		this.lclInstrmBean = lclInstrmBean;
	}

	public String getSeqTp() {
		return seqTp;
	}

	public void setSeqTp(String seqTp) {
		this.seqTp = seqTp;
	}

}