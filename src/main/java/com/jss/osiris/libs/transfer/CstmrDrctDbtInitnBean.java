package com.jss.osiris.libs.transfer;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CstmrDrctDbtInitnBean {
	@JacksonXmlProperty(localName = "GrpHdr")
	GrpHdrBean grpHdrBean;

	@JacksonXmlProperty(localName = "PmtInf")
	@JacksonXmlElementWrapper(useWrapping = false)
	List<PmtInfBean> pmtInfBean;

	public GrpHdrBean getGrpHdrBean() {
		if (grpHdrBean == null)
			grpHdrBean = new GrpHdrBean();
		return grpHdrBean;
	}

	public void setGrpHdrBean(GrpHdrBean grpHdrBean) {
		this.grpHdrBean = grpHdrBean;
	}

	public List<PmtInfBean> getPmtInfBean() {
		if (pmtInfBean == null)
			pmtInfBean = new ArrayList<PmtInfBean>();
		return pmtInfBean;
	}

	public void setPmtInfBean(List<PmtInfBean> pmtInfBean) {
		this.pmtInfBean = pmtInfBean;
	}

}