package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SchmeNmBean {
	@JacksonXmlProperty(localName = "Prtry")
	String prtry;

	public String getPrtry() {
		return prtry;
	}

	public void setPrtry(String prtry) {
		this.prtry = prtry;
	}

}