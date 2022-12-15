package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CdtrAcctBean {
	@JacksonXmlProperty(localName = "Id")
	IdBean idBean;

	public IdBean getIdBean() {
		if (idBean == null)
			idBean = new IdBean();
		return idBean;
	}

	public void setIdBean(IdBean idBean) {
		this.idBean = idBean;
	}

}