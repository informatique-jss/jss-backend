package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class OrgIdBean {
	@JacksonXmlProperty(localName = "OrgId")
	OthrIdBean orgIdBean;

	public OthrIdBean getOrgIdBean() {
		return orgIdBean;
	}

	public void setOrgIdBean(OthrIdBean orgIdBean) {
		this.orgIdBean = orgIdBean;
	}

}