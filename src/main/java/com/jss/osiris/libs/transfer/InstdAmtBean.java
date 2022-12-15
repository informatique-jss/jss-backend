package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class InstdAmtBean {
	@JacksonXmlProperty(localName = "Ccy", isAttribute = true)
	String ccy = "EUR";

	@JacksonXmlText
	String value;

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

	public String getCcy() {
		return ccy;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}