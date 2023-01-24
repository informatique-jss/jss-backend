package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class PrvtOtherBean {
	@JacksonXmlProperty(localName = "SchmeNm")
	SchmeNmBean schmeNmBean;

	@JacksonXmlProperty(localName = "Id")
	String id = "";

	public SchmeNmBean getSchmeNmBean() {
		return schmeNmBean;
	}

	public void setSchmeNmBean(SchmeNmBean schmeNmBean) {
		this.schmeNmBean = schmeNmBean;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}