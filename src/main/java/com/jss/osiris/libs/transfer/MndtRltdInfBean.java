package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MndtRltdInfBean {
	@JacksonXmlProperty(localName = "MndtId")
	String mndtId;

	@JacksonXmlProperty(localName = "DtOfSgntr")
	String dtOfSgntr;

	public String getMndtId() {
		return mndtId;
	}

	public void setMndtId(String mndtId) {
		this.mndtId = mndtId;
	}

	public String getDtOfSgntr() {
		return dtOfSgntr;
	}

	public void setDtOfSgntr(String dtOfSgntr) {
		this.dtOfSgntr = dtOfSgntr;
	}

}