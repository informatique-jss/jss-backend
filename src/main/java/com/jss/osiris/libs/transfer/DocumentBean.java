package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentBean {

	@JacksonXmlProperty(isAttribute = true)
	private final String xmlns = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.03";

	@JacksonXmlProperty(localName = "CstmrCdtTrfInitn")
	CstmrCdtTrfInitnBean cstmrCdtTrfInitnBean;

	public CstmrCdtTrfInitnBean getCstmrCdtTrfInitnBean() {
		if (cstmrCdtTrfInitnBean == null)
			cstmrCdtTrfInitnBean = new CstmrCdtTrfInitnBean();
		return cstmrCdtTrfInitnBean;
	}

	public void setCstmrCdtTrfInitnBean(CstmrCdtTrfInitnBean cstmrCdtTrfInitnBean) {
		this.cstmrCdtTrfInitnBean = cstmrCdtTrfInitnBean;
	}

}