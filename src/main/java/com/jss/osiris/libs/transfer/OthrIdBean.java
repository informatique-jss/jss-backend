package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class OthrIdBean {
	@JacksonXmlProperty(localName = "Othr")
	OthrBean othrBean;

	public OthrBean getOthrBean() {
		if (othrBean == null)
			othrBean = new OthrBean();
		return othrBean;
	}

	public void setOthrBean(OthrBean othrBean) {
		this.othrBean = othrBean;
	}

}