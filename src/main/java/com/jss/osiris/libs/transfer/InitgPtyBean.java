package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class InitgPtyBean {
	@JacksonXmlProperty(localName = "Nm")
	String nm = "";

	@JacksonXmlProperty(localName = "PstlAdr")
	PstlAdrBean pstlAdrBean;

	@JacksonXmlProperty(localName = "Id")
	OrgIdBean idBeanList;

	public void setNm(String nm) {
		this.nm = nm;
	}

	public String getNm() {
		return nm;
	}

	public PstlAdrBean getPstlAdrBean() {
		if (pstlAdrBean == null)
			pstlAdrBean = new PstlAdrBean();
		return pstlAdrBean;
	}

	public void setPstlAdrBean(PstlAdrBean pstlAdrBean) {
		this.pstlAdrBean = pstlAdrBean;
	}

	public OrgIdBean getIdBeanList() {
		return idBeanList;
	}

	public void setIdBeanList(OrgIdBean idBeanList) {
		this.idBeanList = idBeanList;
	}

}