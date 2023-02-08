package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class DrctDbtTxBean {

	@JacksonXmlProperty(localName = "MndtRltdInf")
	MndtRltdInfBean mndtRltdInfBean;

	public MndtRltdInfBean getMndtRltdInfBean() {
		return mndtRltdInfBean;
	}

	public void setMndtRltdInfBean(MndtRltdInfBean mndtRltdInfBean) {
		this.mndtRltdInfBean = mndtRltdInfBean;
	}

}