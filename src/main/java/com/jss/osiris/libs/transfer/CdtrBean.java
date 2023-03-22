package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CdtrBean {
	@JacksonXmlProperty(localName = "Nm")
	String nm;

	public void setNm(String nm) {
		this.nm = nm;
	}

	public String getNm() {
		return nm;
	}

}