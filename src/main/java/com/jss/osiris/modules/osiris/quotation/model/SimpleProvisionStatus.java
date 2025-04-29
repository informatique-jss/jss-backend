package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

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
public class SimpleProvisionStatus implements Serializable, IId {

	/**
	 * WARNINNG : add update in FormaliteStatutsService when adding a new status
	 */
	public static String SIMPLE_PROVISION_NEW = "SIMPLE_PROVISION_NEW";
	public static String SIMPLE_PROVISION_IN_PROGRESS = "SIMPLE_PROVISION_IN_PROGRESS";
	public static String SIMPLE_PROVISION_WAITING_DOCUMENT = "SIMPLE_PROVISION_WAITING_DOCUMENT";
	public static String SIMPLE_PROVISION_WAITING_LINKED_PROVISION = "SIMPLE_PROVISION_WAITING_LINKED_PROVISION";
	public static String SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY = "SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY";
	public static String SIMPLE_PROVISION_DONE = "SIMPLE_PROVISION_DONE";
	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	private Integer id;

	@Column(nullable = false, length = 100)
	@JsonView({ JacksonViews.OsirisDetailedView.class })
	@IndexedField
	private String label;

	@Column(nullable = false, length = 100)
	private String code;

	private String icon;

	private Boolean isOpenState;
	private Boolean isCloseState;

	@OneToMany(targetEntity = SimpleProvisionStatus.class)
	@JoinTable(name = "asso_simple_provision_status_successor", joinColumns = @JoinColumn(name = "id_simple_provision_status"), inverseJoinColumns = @JoinColumn(name = "id_simple_provision_status_successor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<SimpleProvisionStatus> successors;

	@OneToMany(targetEntity = SimpleProvisionStatus.class)
	@JoinTable(name = "asso_simple_provision_status_predecessor", joinColumns = @JoinColumn(name = "id_simple_provision_status"), inverseJoinColumns = @JoinColumn(name = "id_simple_provision_status_predecessor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<SimpleProvisionStatus> predecessors;

	private String aggregateStatus;

	private Integer servicePriority;

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

	public String getAggregateStatus() {
		return aggregateStatus;
	}

	public void setAggregateStatus(String aggregateLabel) {
		this.aggregateStatus = aggregateLabel;
	}

	public static String getSIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY() {
		return SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY;
	}

	public static void setSIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY(
			String sIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY) {
		SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY = sIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY;
	}

	public Integer getServicePriority() {
		return servicePriority;
	}

	public void setServicePriority(Integer servicePriority) {
		this.servicePriority = servicePriority;
	}

}
