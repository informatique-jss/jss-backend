package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class PstlAdrBean {
	@JacksonXmlProperty(localName = "Ctry")
	String ctry = "";

	@JacksonXmlProperty(localName = "AdrLine")
	String adrLine;

	public String getCtry() {
		return ctry;
	}

	public void setCtry(String ctry) {
		this.ctry = ctry;
	}

	public String getAdrLine() {
		return adrLine;
	}

	public void setAdrLine(String adrLine) {
		this.adrLine = adrLine;
	}

}