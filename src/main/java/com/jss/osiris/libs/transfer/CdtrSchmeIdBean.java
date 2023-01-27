package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CdtrSchmeIdBean {
	@JacksonXmlProperty(localName = "Id")
	CdtrSchmeIdBeanIdBean idBean;

	public CdtrSchmeIdBeanIdBean getIdBean() {
		return idBean;
	}

	public void setIdBean(CdtrSchmeIdBeanIdBean idBean) {
		this.idBean = idBean;
	}

}