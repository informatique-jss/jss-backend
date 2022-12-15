package com.jss.osiris.libs.transfer;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CdtTrfTxInfBean {
	@JacksonXmlProperty(localName = "IsMulti")
	String isMulti = "";

	@JacksonXmlProperty(localName = "PmtId")
	PmtIdBean pmtIdBean;

	@JacksonXmlProperty(localName = "Amt")
	AmtBean amtBean;

	@JacksonXmlProperty(localName = "CdtrAgt")
	List<CdtrAgtBean> cdtrAgtBeanList;

	@JacksonXmlProperty(localName = "Cdtr")
	List<CdtrBean> cdtrBeanList;

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

	public void setCdtrBeanList(List<CdtrBean> cdtrBeanList) {
		this.cdtrBeanList = cdtrBeanList;
	}

	public List<CdtrBean> getCdtrBeanList() {
		if (cdtrBeanList == null)
			cdtrBeanList = new ArrayList<CdtrBean>();
		return cdtrBeanList;
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