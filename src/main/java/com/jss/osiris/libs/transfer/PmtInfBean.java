package com.jss.osiris.libs.transfer;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonPropertyOrder({ "PmtInfId", "PmtMtd", "BtchBookg", "NbOfTxs", "CtrlSum", "PmtTpInf", "InstrPrty", "SvcLvl", "Cd",
		"CtgyPurp", "ReqdExctnDt" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PmtInfBean {
	@JacksonXmlProperty(localName = "BtchBookg")
	Boolean btchBookg = true;

	@JacksonXmlProperty(localName = "CtrlSum")
	Float ctrlSum = 0f;

	@JacksonXmlProperty(localName = "NbOfTxs")
	Integer nbOfTxs = 0;

	@JacksonXmlProperty(localName = "PmtInfId")
	String pmtInfId;

	@JacksonXmlProperty(localName = "PmtMtd")
	String pmtMtd;

	@JacksonXmlProperty(localName = "ReqdExctnDt")
	String reqdExctnDt;

	@JacksonXmlProperty(localName = "ReqdColltnDt")
	String reqdColltnDt;

	@JacksonXmlProperty(localName = "PmtTpInf")
	PmtTpInfBean pmtTpInfBean;

	@JacksonXmlProperty(localName = "Dbtr")
	DbtrBean dbtrBean;

	@JacksonXmlProperty(localName = "Cdtr")
	DbtrBean cdtrBean;

	@JacksonXmlProperty(localName = "DbtrAcct")
	DbtrAcctBean dbtrAcctBean;

	@JacksonXmlProperty(localName = "CdtrAcct")
	DbtrAcctBean cdtrAcctBean;

	@JacksonXmlProperty(localName = "DbtrAgt")
	DbtrAgtBean dbtrAgtBean;

	@JacksonXmlProperty(localName = "CdtrAgt")
	DbtrAgtBean cdtrAgtBean;

	@JacksonXmlProperty(localName = "CdtrSchmeId")
	CdtrSchmeIdBean cdtrSchmeIdBean;

	@JacksonXmlProperty(localName = "CdtTrfTxInf")
	@JacksonXmlElementWrapper(useWrapping = false)
	List<CdtTrfTxInfBean> cdtTrfTxInfBeanList;

	@JacksonXmlProperty(localName = "DrctDbtTxInf")
	List<DrctDbtTxInfBean> drctDbtTxInfBeanList;

	@JacksonXmlProperty(localName = "ChrgBr")
	String ChrgBr;

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

	public String getReqdColltnDt() {
		return reqdColltnDt;
	}

	public void setReqdColltnDt(String reqdColltnDt) {
		this.reqdColltnDt = reqdColltnDt;
	}

	public DbtrBean getCdtrBean() {
		return cdtrBean;
	}

	public void setCdtrBean(DbtrBean cdtrBean) {
		this.cdtrBean = cdtrBean;
	}

	public DbtrAcctBean getCdtrAcctBean() {
		return cdtrAcctBean;
	}

	public void setCdtrAcctBean(DbtrAcctBean cdtrAcctBean) {
		this.cdtrAcctBean = cdtrAcctBean;
	}

	public DbtrAgtBean getCdtrAgtBean() {
		return cdtrAgtBean;
	}

	public void setCdtrAgtBean(DbtrAgtBean cdtrAgtBean) {
		this.cdtrAgtBean = cdtrAgtBean;
	}

	public String getChrgBr() {
		return ChrgBr;
	}

	public void setChrgBr(String chrgBr) {
		ChrgBr = chrgBr;
	}

	public List<DrctDbtTxInfBean> getDrctDbtTxInfBeanList() {
		return drctDbtTxInfBeanList;
	}

	public void setDrctDbtTxInfBeanList(List<DrctDbtTxInfBean> drctDbtTxInfBeanList) {
		this.drctDbtTxInfBeanList = drctDbtTxInfBeanList;
	}

	public CdtrSchmeIdBean getCdtrSchmeIdBean() {
		return cdtrSchmeIdBean;
	}

	public void setCdtrSchmeIdBean(CdtrSchmeIdBean cdtrSchmeIdBean) {
		this.cdtrSchmeIdBean = cdtrSchmeIdBean;
	}

}