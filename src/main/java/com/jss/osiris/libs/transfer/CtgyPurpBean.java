package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CtgyPurpBean {
	@JacksonXmlProperty(localName = "Cd")
	String cd;

	public void setCd(String cd) {
		this.cd = cd;
	}

	public String getCd() {
		return cd;
	}

}