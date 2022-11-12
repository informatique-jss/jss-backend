package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class CustomerOrderStatus implements Serializable, IId {

	/**
	 * WARNINNG : add update in Service when adding a new status
	 */
	public static String OPEN = "OPEN";
	public static String TO_VERIFY = "TO_VERIFY";
	public static String VALIDATED_BY_JSS = "VALIDATED_BY_JSS";
	public static String SENT_TO_CUSTOMER = "SENT_TO_CUSTOMER";
	public static String VALIDATED_BY_CUSTOMER = "VALIDATED_BY_CUSTOMER";
	public static String REFUSED_BY_CUSTOMER = "REFUSED_BY_CUSTOMER";
	public static String ABANDONED = "ABANDONED";
	public static String BILLED = "BILLED";
	public static String WAITING_DEPOSIT = "WAITING_DEPOSIT";
	public static String BEING_PROCESSED = "BEING_PROCESSED";
	public static String TO_BILLED = "TO_BILLED";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false, length = 100)
	private String label;

	@Column(nullable = false, length = 30)
	private String code;

	private String icon;

	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name = "asso_customer_order_status_successor", joinColumns = @JoinColumn(name = "id_customer_order_status"), inverseJoinColumns = @JoinColumn(name = "id_customer_order_status_successor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<CustomerOrderStatus> successors;

	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name = "asso_customer_order_status_predecessor", joinColumns = @JoinColumn(name = "id_customer_order_status"), inverseJoinColumns = @JoinColumn(name = "id_customer_order_status_predecessor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<CustomerOrderStatus> predecessors;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public static String getOPEN() {
		return OPEN;
	}

	public static void setOPEN(String oPEN) {
		OPEN = oPEN;
	}

	public static String getTO_VERIFY() {
		return TO_VERIFY;
	}

	public static void setTO_VERIFY(String tO_VERIFY) {
		TO_VERIFY = tO_VERIFY;
	}

	public static String getVALIDATED_BY_JSS() {
		return VALIDATED_BY_JSS;
	}

	public static void setVALIDATED_BY_JSS(String vALIDATED_BY_JSS) {
		VALIDATED_BY_JSS = vALIDATED_BY_JSS;
	}

	public static String getSENT_TO_CUSTOMER() {
		return SENT_TO_CUSTOMER;
	}

	public static void setSENT_TO_CUSTOMER(String sENT_TO_CUSTOMER) {
		SENT_TO_CUSTOMER = sENT_TO_CUSTOMER;
	}

	public static String getVALIDATED_BY_CUSTOMER() {
		return VALIDATED_BY_CUSTOMER;
	}

	public static void setVALIDATED_BY_CUSTOMER(String vALIDATED_BY_CUSTOMER) {
		VALIDATED_BY_CUSTOMER = vALIDATED_BY_CUSTOMER;
	}

	public static String getREFUSED_BY_CUSTOMER() {
		return REFUSED_BY_CUSTOMER;
	}

	public static void setREFUSED_BY_CUSTOMER(String rEFUSED_BY_CUSTOMER) {
		REFUSED_BY_CUSTOMER = rEFUSED_BY_CUSTOMER;
	}

	public static String getABANDONED() {
		return ABANDONED;
	}

	public static void setABANDONED(String aBANDONED) {
		ABANDONED = aBANDONED;
	}

	public static String getBILLED() {
		return BILLED;
	}

	public static void setBILLED(String bILLED) {
		BILLED = bILLED;
	}

	public static String getWAITING_DEPOSIT() {
		return WAITING_DEPOSIT;
	}

	public static void setWAITING_DEPOSIT(String wAITING_DEPOSIT) {
		WAITING_DEPOSIT = wAITING_DEPOSIT;
	}

	public static String getBEING_PROCESSED() {
		return BEING_PROCESSED;
	}

	public static void setBEING_PROCESSED(String bEING_PROCESSED) {
		BEING_PROCESSED = bEING_PROCESSED;
	}

	public static String getTO_BILLED() {
		return TO_BILLED;
	}

	public static void setTO_BILLED(String tO_BILLED) {
		TO_BILLED = tO_BILLED;
	}

	public List<CustomerOrderStatus> getSuccessors() {
		return successors;
	}

	public void setSuccessors(List<CustomerOrderStatus> successors) {
		this.successors = successors;
	}

	public List<CustomerOrderStatus> getPredecessors() {
		return predecessors;
	}

	public void setPredecessors(List<CustomerOrderStatus> predecessors) {
		this.predecessors = predecessors;
	}

}
