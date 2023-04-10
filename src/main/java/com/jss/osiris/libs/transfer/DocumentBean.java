package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JacksonXmlRootElement(localName = "Document")
public class DocumentBean {

	@JacksonXmlProperty(isAttribute = true)
	private String xmlns = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.03";

	@JacksonXmlProperty(localName = "CstmrCdtTrfInitn")
	CstmrCdtTrfInitnBean cstmrCdtTrfInitnBean;

	@JacksonXmlProperty(localName = "CstmrDrctDbtInitn")
	CstmrDrctDbtInitnBean cstmrDrctDbtInitn;

	public CstmrCdtTrfInitnBean getCstmrCdtTrfInitnBean() {
		return cstmrCdtTrfInitnBean;
	}

	public void setCstmrCdtTrfInitnBean(CstmrCdtTrfInitnBean cstmrCdtTrfInitnBean) {
		this.cstmrCdtTrfInitnBean = cstmrCdtTrfInitnBean;
	}

	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	public CstmrDrctDbtInitnBean getCstmrDrctDbtInitn() {
		return cstmrDrctDbtInitn;
	}

	public void setCstmrDrctDbtInitn(CstmrDrctDbtInitnBean cstmrDrctDbtInitn) {
		this.cstmrDrctDbtInitn = cstmrDrctDbtInitn;
	}

}