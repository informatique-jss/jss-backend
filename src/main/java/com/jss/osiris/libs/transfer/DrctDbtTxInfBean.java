package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class DrctDbtTxInfBean {
	@JacksonXmlProperty(localName = "IsMulti")
	String isMulti = "";

	@JacksonXmlProperty(localName = "PmtId")
	PmtIdBean pmtIdBean;

	@JacksonXmlProperty(localName = "InstdAmt")
	InstdAmtBean instdAmtBean;

	@JacksonXmlProperty(localName = "DrctDbtTx")
	DrctDbtTxBean drctDbtTxBean;

	@JacksonXmlProperty(localName = "DbtrAgt")
	DbtrAgtBean dbtrAgtBean;

	@JacksonXmlProperty(localName = "Dbtr")
	DbtrBean dbtrBean;

	@JacksonXmlProperty(localName = "DbtrAcct")
	DbtrAcctBean dbtrAcctBean;

	@JacksonXmlProperty(localName = "RmtInf")
	RmtInfBean rmtInfBean;

	public String getIsMulti() {
		return isMulti;
	}

	public void setIsMulti(String isMulti) {
		this.isMulti = isMulti;
	}

	public PmtIdBean getPmtIdBean() {
		return pmtIdBean;
	}

	public void setPmtIdBean(PmtIdBean pmtIdBean) {
		this.pmtIdBean = pmtIdBean;
	}

	public DrctDbtTxBean getDrctDbtTxBean() {
		return drctDbtTxBean;
	}

	public void setDrctDbtTxBean(DrctDbtTxBean drctDbtTxBean) {
		this.drctDbtTxBean = drctDbtTxBean;
	}

	public DbtrAgtBean getDbtrAgtBean() {
		return dbtrAgtBean;
	}

	public void setDbtrAgtBean(DbtrAgtBean dbtrAgtBean) {
		this.dbtrAgtBean = dbtrAgtBean;
	}

	public DbtrBean getDbtrBean() {
		return dbtrBean;
	}

	public void setDbtrBean(DbtrBean dbtrBean) {
		this.dbtrBean = dbtrBean;
	}

	public DbtrAcctBean getDbtrAcctBean() {
		return dbtrAcctBean;
	}

	public void setDbtrAcctBean(DbtrAcctBean dbtrAcctBean) {
		this.dbtrAcctBean = dbtrAcctBean;
	}

	public RmtInfBean getRmtInfBean() {
		return rmtInfBean;
	}

	public void setRmtInfBean(RmtInfBean rmtInfBean) {
		this.rmtInfBean = rmtInfBean;
	}

	public InstdAmtBean getInstdAmtBean() {
		return instdAmtBean;
	}

	public void setInstdAmtBean(InstdAmtBean instdAmtBean) {
		this.instdAmtBean = instdAmtBean;
	}

}