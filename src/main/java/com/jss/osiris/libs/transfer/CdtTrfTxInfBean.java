package com.jss.osiris.libs.transfer;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonPropertyOrder({ "PmtId", "InstrId", "EndToEndId", "Amt", "InstdAmt", "UltmtDbtr" })
public class CdtTrfTxInfBean {
	@JacksonXmlProperty(localName = "IsMulti")
	String isMulti = "";

	@JacksonXmlProperty(localName = "PmtId")
	PmtIdBean pmtIdBean;

	@JacksonXmlProperty(localName = "Amt")
	AmtBean amtBean;

	@JacksonXmlProperty(localName = "CdtrAgt")
	@JacksonXmlElementWrapper(useWrapping = false)
	List<CdtrAgtBean> cdtrAgtBeanList;

	@JacksonXmlProperty(localName = "Cdtr")
	@JacksonXmlElementWrapper(useWrapping = false)
	CdtrBean cdtrBean;

	@JacksonXmlProperty(localName = "CdtrAcct")
	CdtrAcctBean cdtrAcctBean;

	@JacksonXmlProperty(localName = "RmtInf")
	RmtInfBean rmtInfBean;

	public void setIsMulti(String isMulti) {
		this.isMulti = isMulti;
	}

	public String getIsMulti() {
		return isMulti;
	}

	public PmtIdBean getPmtIdBean() {
		if (pmtIdBean == null)
			pmtIdBean = new PmtIdBean();
		return pmtIdBean;
	}

	public void setPmtIdBean(PmtIdBean pmtIdBean) {
		this.pmtIdBean = pmtIdBean;
	}

	public AmtBean getAmtBean() {
		if (amtBean == null)
			amtBean = new AmtBean();
		return amtBean;
	}

	public void setAmtBean(AmtBean amtBean) {
		this.amtBean = amtBean;
	}

	public void setCdtrAgtBeanList(List<CdtrAgtBean> cdtrAgtBeanList) {
		this.cdtrAgtBeanList = cdtrAgtBeanList;
	}

	public List<CdtrAgtBean> getCdtrAgtBeanList() {
		if (cdtrAgtBeanList == null)
			cdtrAgtBeanList = new ArrayList<CdtrAgtBean>();
		return cdtrAgtBeanList;
	}

	public void setCdtrBean(CdtrBean cdtrBean) {
		this.cdtrBean = cdtrBean;
	}

	public CdtrBean getCdtrBean() {
		if (cdtrBean == null)
			cdtrBean = new CdtrBean();
		return cdtrBean;
	}

	public CdtrAcctBean getCdtrAcctBean() {
		if (cdtrAcctBean == null)
			cdtrAcctBean = new CdtrAcctBean();
		return cdtrAcctBean;
	}

	public void setCdtrAcctBean(CdtrAcctBean cdtrAcctBean) {
		this.cdtrAcctBean = cdtrAcctBean;
	}

	public RmtInfBean getRmtInfBean() {
		if (rmtInfBean == null)
			rmtInfBean = new RmtInfBean();
		return rmtInfBean;
	}

	public void setRmtInfBean(RmtInfBean rmtInfBean) {
		this.rmtInfBean = rmtInfBean;
	}

}