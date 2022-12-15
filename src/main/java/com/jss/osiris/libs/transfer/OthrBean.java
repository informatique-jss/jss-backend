package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class OthrBean {
	@JacksonXmlProperty(localName = "Id")
	String id = "";

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

}