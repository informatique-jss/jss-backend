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

@Entity
public class QuotationStatus extends IWorkflowElement implements Serializable {
	/**
	 * WARNINNG : add update in QuotationStatutsService when adding a new status
	 */
	public static String OPEN = "OPEN";
	public static String TO_VERIFY = "TO_VERIFY";
	public static String QUOTATION_WAITING_CONFRERE = "QUOTATION_WAITING_CONFRERE";
	public static String SENT_TO_CUSTOMER = "SENT_TO_CUSTOMER";
	public static String VALIDATED_BY_CUSTOMER = "VALIDATED_BY_CUSTOMER";
	public static String REFUSED_BY_CUSTOMER = "REFUSED_BY_CUSTOMER";
	public static String ABANDONED = "ABANDONED";

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
	@JoinTable(name = "asso_quotation_status_successor", joinColumns = @JoinColumn(name = "id_quotation_status"), inverseJoinColumns = @JoinColumn(name = "id_quotation_status_successor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<QuotationStatus> successors;

	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name = "asso_quotation_status_predecessor", joinColumns = @JoinColumn(name = "id_quotation_status"), inverseJoinColumns = @JoinColumn(name = "id_quotation_status_predecessor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<QuotationStatus> predecessors;

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

	public void setCode(String code) {
		this.code = code;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public List<QuotationStatus> getSuccessors() {
		return successors;
	}

	@SuppressWarnings({ "unchecked" })
	public void setSuccessors(List<? extends IWorkflowElement> successors) {
		this.successors = (List<QuotationStatus>) successors;
	}

	public List<QuotationStatus> getPredecessors() {
		return predecessors;
	}

	@SuppressWarnings({ "unchecked" })
	public void setPredecessors(List<? extends IWorkflowElement> predecessors) {
		this.predecessors = (List<QuotationStatus>) predecessors;
	}

}
