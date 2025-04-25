package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class CustomerOrderStatus extends IWorkflowElement implements Serializable {

	/**
	 * WARNINNG : add update in Service when adding a new status
	 */
	public static String DRAFT = "OPEN";
	public static String ABANDONED = "ABANDONED";
	public static String BILLED = "BILLED";
	public static String WAITING_DEPOSIT = "WAITING_DEPOSIT";
	public static String BEING_PROCESSED = "BEING_PROCESSED";
	public static String TO_BILLED = "TO_BILLED";
	public static String PAYED = "PAYED";

	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class })
	private Integer id;

	@Column(nullable = false, length = 100)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class })
	@IndexedField
	private String label;

	@Column(nullable = false, length = 100)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class })
	private String code;

	private String icon;

	@OneToMany(targetEntity = CustomerOrderStatus.class)
	@JoinTable(name = "asso_customer_order_status_successor", joinColumns = @JoinColumn(name = "id_customer_order_status"), inverseJoinColumns = @JoinColumn(name = "id_customer_order_status_successor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<CustomerOrderStatus> successors;

	@OneToMany(targetEntity = CustomerOrderStatus.class)
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

	public static String getDRAFT() {
		return DRAFT;
	}

	public static void setDRAFT(String oPEN) {
		DRAFT = oPEN;
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

	@SuppressWarnings({ "unchecked" })
	public void setSuccessors(List<? extends IWorkflowElement> successors) {
		this.successors = (List<CustomerOrderStatus>) successors;
	}

	public List<CustomerOrderStatus> getPredecessors() {
		return predecessors;
	}

	@SuppressWarnings({ "unchecked" })
	public void setPredecessors(List<? extends IWorkflowElement> predecessors) {
		this.predecessors = (List<CustomerOrderStatus>) predecessors;
	}

}
