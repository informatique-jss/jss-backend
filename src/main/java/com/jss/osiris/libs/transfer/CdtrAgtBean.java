package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CdtrAgtBean {
	@JacksonXmlProperty(localName = "FinInstnId")
	FinInstnIdBean finInstnIdBean;

	public FinInstnIdBean getFinInstnIdBean() {
		if (finInstnIdBean == null)
			finInstnIdBean = new FinInstnIdBean();
		return finInstnIdBean;
	}

	public void setFinInstnIdBean(FinInstnIdBean finInstnIdBean) {
		this.finInstnIdBean = finInstnIdBean;
	}

}