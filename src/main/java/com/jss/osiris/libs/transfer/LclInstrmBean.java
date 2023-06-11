package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class LclInstrmBean {
	@JacksonXmlProperty(localName = "Cd")
	String cd = "";

	public void setCd(String cd) {
		this.cd = cd;
	}

	public String getCd() {
		return cd;
	}

}