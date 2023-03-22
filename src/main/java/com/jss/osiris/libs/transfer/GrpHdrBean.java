package com.jss.osiris.libs.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonPropertyOrder({ "msgId", "creDtTm", "nbOfTxs", "ctrlSum" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GrpHdrBean {
	@JacksonXmlProperty(localName = "CreDtTm")
	String creDtTm;

	@JacksonXmlProperty(localName = "CtrlSum")
	Float ctrlSum = 0f;

	@JacksonXmlProperty(localName = "MsgId")
	String msgId;

	@JacksonXmlProperty(localName = "NbOfTxs")
	Integer nbOfTxs = 0;

	@JacksonXmlProperty(localName = "InitgPty")
	InitgPtyBean initgPtyBean;

	public void setCreDtTm(String creDtTm) {
		this.creDtTm = creDtTm;
	}

	public String getCreDtTm() {
		return creDtTm;
	}

	public void setCtrlSum(Float ctrlSum) {
		this.ctrlSum = ctrlSum;
	}

	public Float getCtrlSum() {
		return ctrlSum;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setNbOfTxs(Integer nbOfTxs) {
		this.nbOfTxs = nbOfTxs;
	}

	public Integer getNbOfTxs() {
		return nbOfTxs;
	}

	public InitgPtyBean getInitgPtyBean() {
		if (initgPtyBean == null)
			initgPtyBean = new InitgPtyBean();
		return initgPtyBean;
	}

	public void setInitgPtyBean(InitgPtyBean initgPtyBean) {
		this.initgPtyBean = initgPtyBean;
	}

}