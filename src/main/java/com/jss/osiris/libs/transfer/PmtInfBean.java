package com.jss.osiris.libs.transfer;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class PmtInfBean {
	@JacksonXmlProperty(localName = "BtchBookg")
	Boolean btchBookg = true;

	@JacksonXmlProperty(localName = "CtrlSum")
	Float ctrlSum = 0f;

	@JacksonXmlProperty(localName = "NbOfTxs")
	Integer nbOfTxs = 0;

	@JacksonXmlProperty(localName = "PmtInfId")
	String pmtInfId = "";

	@JacksonXmlProperty(localName = "PmtMtd")
	String pmtMtd = "";

	@JacksonXmlProperty(localName = "ReqdExctnDt")
	String reqdExctnDt = "";

	@JacksonXmlProperty(localName = "PmtTpInf")
	PmtTpInfBean pmtTpInfBean;

	@JacksonXmlProperty(localName = "Dbtr")
	DbtrBean dbtrBean;

	@JacksonXmlProperty(localName = "DbtrAcct")
	DbtrAcctBean dbtrAcctBean;

	@JacksonXmlProperty(localName = "DbtrAgt")
	DbtrAgtBean dbtrAgtBean;

	@JacksonXmlProperty(localName = "CdtTrfTxInf")
	List<CdtTrfTxInfBean> cdtTrfTxInfBeanList;

	public void setBtchBookg(Boolean btchBookg) {
		this.btchBookg = btchBookg;
	}

	public Boolean getBtchBookg() {
		return btchBookg;
	}

	public void setCtrlSum(Float ctrlSum) {
		this.ctrlSum = ctrlSum;
	}

	public Float getCtrlSum() {
		return ctrlSum;
	}

	public void setNbOfTxs(Integer nbOfTxs) {
		this.nbOfTxs = nbOfTxs;
	}

	public Integer getNbOfTxs() {
		return nbOfTxs;
	}

	public void setPmtInfId(String pmtInfId) {
		this.pmtInfId = pmtInfId;
	}

	public String getPmtInfId() {
		return pmtInfId;
	}

	public void setPmtMtd(String pmtMtd) {
		this.pmtMtd = pmtMtd;
	}

	public String getPmtMtd() {
		return pmtMtd;
	}

	public void setReqdExctnDt(String reqdExctnDt) {
		this.reqdExctnDt = reqdExctnDt;
	}

	public String getReqdExctnDt() {
		return reqdExctnDt;
	}

	public PmtTpInfBean getPmtTpInfBean() {
		if (pmtTpInfBean == null)
			pmtTpInfBean = new PmtTpInfBean();
		return pmtTpInfBean;
	}

	public void setPmtTpInfBean(PmtTpInfBean pmtTpInfBean) {
		this.pmtTpInfBean = pmtTpInfBean;
	}

	public DbtrBean getDbtrBean() {
		if (dbtrBean == null)
			dbtrBean = new DbtrBean();
		return dbtrBean;
	}

	public void setDbtrBean(DbtrBean dbtrBean) {
		this.dbtrBean = dbtrBean;
	}

	public DbtrAcctBean getDbtrAcctBean() {
		if (dbtrAcctBean == null)
			dbtrAcctBean = new DbtrAcctBean();
		return dbtrAcctBean;
	}

	public void setDbtrAcctBean(DbtrAcctBean dbtrAcctBean) {
		this.dbtrAcctBean = dbtrAcctBean;
	}

	public DbtrAgtBean getDbtrAgtBean() {
		if (dbtrAgtBean == null)
			dbtrAgtBean = new DbtrAgtBean();
		return dbtrAgtBean;
	}

	public void setDbtrAgtBean(DbtrAgtBean dbtrAgtBean) {
		this.dbtrAgtBean = dbtrAgtBean;
	}

	public void setCdtTrfTxInfBeanList(List<CdtTrfTxInfBean> cdtTrfTxInfBeanList) {
		this.cdtTrfTxInfBeanList = cdtTrfTxInfBeanList;
	}

	public List<CdtTrfTxInfBean> getCdtTrfTxInfBeanList() {
		if (cdtTrfTxInfBeanList == null)
			cdtTrfTxInfBeanList = new ArrayList<CdtTrfTxInfBean>();
		return cdtTrfTxInfBeanList;
	}

}