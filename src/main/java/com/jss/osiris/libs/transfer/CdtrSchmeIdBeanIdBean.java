package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CdtrSchmeIdBeanIdBean {
	@JacksonXmlProperty(localName = "PrvtId")
	PrvtIdBean prvtIdBean;

	public PrvtIdBean getPrvtIdBean() {
		return prvtIdBean;
	}

	public void setPrvtIdBean(PrvtIdBean prvtIdBean) {
		this.prvtIdBean = prvtIdBean;
	}

}