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

	@Column(nullable = false, length = 100)
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
