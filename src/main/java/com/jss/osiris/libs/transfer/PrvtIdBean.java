package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class PrvtIdBean {
	@JacksonXmlProperty(localName = "Othr")
	PrvtOtherBean prvtOtherBean;

	public PrvtOtherBean getPrvtOtherBean() {
		return prvtOtherBean;
	}

	public void setPrvtOtherBean(PrvtOtherBean prvtOtherBean) {
		this.prvtOtherBean = prvtOtherBean;
	}

}