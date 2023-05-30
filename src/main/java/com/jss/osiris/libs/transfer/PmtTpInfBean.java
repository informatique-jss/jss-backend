package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonPropertyOrder({ "SvcLvl", "LclInstrm", "SeqTp", "CtgyPurp" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PmtTpInfBean {
	@JacksonXmlProperty(localName = "InstrPrty")
	String instrPrty;

	@JacksonXmlProperty(localName = "SvcLvl")
	SvcLvlBean svcLvlBean;

	@JacksonXmlProperty(localName = "CtgyPurp")
	CtgyPurpBean ctgyPurpBean;

	@JacksonXmlProperty(localName = "LclInstrm")
	LclInstrmBean lclInstrmBean;

	@JacksonXmlProperty(localName = "SeqTp")
	String seqTp;

	public void setInstrPrty(String instrPrty) {
		this.instrPrty = instrPrty;
	}

	public String getInstrPrty() {
		return instrPrty;
	}

	public SvcLvlBean getSvcLvlBean() {
		return svcLvlBean;
	}

	public void setSvcLvlBean(SvcLvlBean svcLvlBean) {
		this.svcLvlBean = svcLvlBean;
	}

	public CtgyPurpBean getCtgyPurpBean() {
		return ctgyPurpBean;
	}

	public void setCtgyPurpBean(CtgyPurpBean ctgyPurpBean) {
		this.ctgyPurpBean = ctgyPurpBean;
	}

	public LclInstrmBean getLclInstrmBean() {
		return lclInstrmBean;
	}

	public void setLclInstrmBean(LclInstrmBean lclInstrmBean) {
		this.lclInstrmBean = lclInstrmBean;
	}

	public String getSeqTp() {
		return seqTp;
	}

	public void setSeqTp(String seqTp) {
		this.seqTp = seqTp;
	}

}