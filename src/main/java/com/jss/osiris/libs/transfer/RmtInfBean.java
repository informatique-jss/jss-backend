package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class RmtInfBean {
	@JacksonXmlProperty(localName = "Ustrd")
	String ustrd = "";

	public void setUstrd(String ustrd) {
		this.ustrd = ustrd;
	}

	public String getUstrd() {
		return ustrd;
	}

}