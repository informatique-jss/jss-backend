package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class AmtBean {
	@JacksonXmlProperty(localName = "InstdAmt")
	InstdAmtBean instdAmtBean;

	public InstdAmtBean getInstdAmtBean() {
		if (instdAmtBean == null)
			instdAmtBean = new InstdAmtBean();
		return instdAmtBean;
	}

	public void setInstdAmtBean(InstdAmtBean instdAmtBean) {
		this.instdAmtBean = instdAmtBean;
	}

}