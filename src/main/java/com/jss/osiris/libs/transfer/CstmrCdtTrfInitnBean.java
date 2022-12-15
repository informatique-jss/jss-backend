package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CstmrCdtTrfInitnBean {
	@JacksonXmlProperty(localName = "GrpHdr")
	GrpHdrBean grpHdrBean;

	@JacksonXmlProperty(localName = "PmtInf")
	PmtInfBean pmtInfBean;

	public GrpHdrBean getGrpHdrBean() {
		if (grpHdrBean == null)
			grpHdrBean = new GrpHdrBean();
		return grpHdrBean;
	}

	public void setGrpHdrBean(GrpHdrBean grpHdrBean) {
		this.grpHdrBean = grpHdrBean;
	}

	public PmtInfBean getPmtInfBean() {
		if (pmtInfBean == null)
			pmtInfBean = new PmtInfBean();
		return pmtInfBean;
	}

	public void setPmtInfBean(PmtInfBean pmtInfBean) {
		this.pmtInfBean = pmtInfBean;
	}

}