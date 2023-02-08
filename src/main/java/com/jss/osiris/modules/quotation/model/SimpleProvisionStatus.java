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
public class SimpleProvisionStatus implements Serializable, IId {

	/**
	 * WARNINNG : add update in FormaliteStatutsService when adding a new status
	 */
	public static String SIMPLE_PROVISION_NEW = "SIMPLE_PROVISION_NEW";
	public static String SIMPLE_PROVISION_IN_PROGRESS = "SIMPLE_PROVISION_IN_PROGRESS";
	public static String SIMPLE_PROVISION_WAITING_DOCUMENT = "SIMPLE_PROVISION_WAITING_DOCUMENT";
	public static String SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY = "SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY";
	public static String SIMPLE_PROVISION_DONE = "SIMPLE_PROVISION_DONE";
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false, length = 100)
	private String label;

	@Column(nullable = false, length = 100)
	private String code;

	private String icon;

	private Boolean isOpenState;
	private Boolean isCloseState;

	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name = "asso_simple_provision_status_successor", joinColumns = @JoinColumn(name = "id_simple_provision_status"), inverseJoinColumns = @JoinColumn(name = "id_simple_provision_status_successor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<SimpleProvisionStatus> successors;

	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name = "asso_simple_provision_status_predecessor", joinColumns = @JoinColumn(name = "id_simple_provision_status"), inverseJoinColumns = @JoinColumn(name = "id_simple_provision_status_predecessor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<SimpleProvisionStatus> predecessors;

	private String aggregateLabel;

	public static String getSIMPLE_PROVISION_NEW() {
		return SIMPLE_PROVISION_NEW;
	}

	public static void setSIMPLE_PROVISION_NEW(String sIMPLE_PROVISION_NEW) {
		SIMPLE_PROVISION_NEW = sIMPLE_PROVISION_NEW;
	}

	public static String getSIMPLE_PROVISION_IN_PROGRESS() {
		return SIMPLE_PROVISION_IN_PROGRESS;
	}

	public static void setSIMPLE_PROVISION_IN_PROGRESS(String sIMPLE_PROVISION_IN_PROGRESS) {
		SIMPLE_PROVISION_IN_PROGRESS = sIMPLE_PROVISION_IN_PROGRESS;
	}

	public static String getSIMPLE_PROVISION_WAITING_DOCUMENT() {
		return SIMPLE_PROVISION_WAITING_DOCUMENT;
	}

	public static void setSIMPLE_PROVISION_WAITING_DOCUMENT(String sIMPLE_PROVISION_WAITING_DOCUMENT) {
		SIMPLE_PROVISION_WAITING_DOCUMENT = sIMPLE_PROVISION_WAITING_DOCUMENT;
	}

	public static String getSIMPLE_PROVISION_DONE() {
		return SIMPLE_PROVISION_DONE;
	}

	public static void setSIMPLE_PROVISION_DONE(String sIMPLE_PROVISION_DONE) {
		SIMPLE_PROVISION_DONE = sIMPLE_PROVISION_DONE;
	}

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

	public Boolean getIsOpenState() {
		return isOpenState;
	}

	public void setIsOpenState(Boolean isOpenState) {
		this.isOpenState = isOpenState;
	}

	public Boolean getIsCloseState() {
		return isCloseState;
	}

	public void setIsCloseState(Boolean isCloseState) {
		this.isCloseState = isCloseState;
	}

	public List<SimpleProvisionStatus> getSuccessors() {
		return successors;
	}

	public void setSuccessors(List<SimpleProvisionStatus> successors) {
		this.successors = successors;
	}

	public List<SimpleProvisionStatus> getPredecessors() {
		return predecessors;
	}

	public void setPredecessors(List<SimpleProvisionStatus> predecessors) {
		this.predecessors = predecessors;
	}

	public String getAggregateLabel() {
		return aggregateLabel;
	}

	public void setAggregateLabel(String aggregateLabel) {
		this.aggregateLabel = aggregateLabel;
	}

	public static String getSIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY() {
		return SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY;
	}

	public static void setSIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY(
			String sIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY) {
		SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY = sIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY;
	}

}
