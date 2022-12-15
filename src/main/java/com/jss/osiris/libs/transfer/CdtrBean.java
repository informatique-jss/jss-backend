package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CdtrBean {
	@JacksonXmlProperty(localName = "Nm")
	String nm = "";

	public void setNm(String nm) {
		this.nm = nm;
	}

	public String getNm() {
		return nm;
	}

}