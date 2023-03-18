package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonPropertyOrder({ "InstrId", "EndToEndId" })
public class PmtIdBean {
	@JacksonXmlProperty(localName = "EndToEndId")
	String endToEndId = "";

	@JacksonXmlProperty(localName = "InstrId")
	String instrId = "";

	public void setEndToEndId(String endToEndId) {
		this.endToEndId = endToEndId;
	}

	public String getEndToEndId() {
		return endToEndId;
	}

	public void setInstrId(String instrId) {
		this.instrId = instrId;
	}

	public String getInstrId() {
		return instrId;
	}

}