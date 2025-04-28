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
public class FormaliteStatus implements Serializable, IId {

	/**
	 * WARNINNG : add update in FormaliteStatutsService when adding a new status
	 */
	public static String FORMALITE_NEW = "FORMALITE_NEW";
	public static String FORMALITE_IN_PROGRESS = "FORMALITE_IN_PROGRESS";
	public static String FORMALITE_WAITING_DOCUMENT = "FORMALITE_WAITING_DOCUMENT";
	public static String FORMALITE_WAITING_DOCUMENT_AUTHORITY = "FORMALITE_WAITING_DOCUMENT_AUTHORITY";
	public static String FORMALITE_WAITING_LINKED_PROVISION = "FORMALITE_WAITING_LINKED_PROVISION";
	public static String FORMALITE_AUTHORITY_REJECTED = "FORMALITE_AUTHORITY_REJECTED";
	public static String FORMALITE_AUTHORITY_VALIDATED = "FORMALITE_AUTHORITY_VALIDATED";
	public static String FORMALITE_AUTHORITY_TECHNICAL_BLOCKING = "FORMALITE_AUTHORITY_TECHNICAL_BLOCKING";
	public static String FORMALITE_DONE = "FORMALITE_DONE";
	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	private Integer id;

	@Column(nullable = false, length = 100)
	@IndexedField
	@JsonView({ JacksonViews.OsirisDetailedView.class })
	private String label;

	@Column(nullable = false, length = 100)
	private String code;

	private String icon;

	private Boolean isOpenState;
	private Boolean isCloseState;

	@OneToMany(targetEntity = FormaliteStatus.class)
	@JoinTable(name = "asso_formalite_status_successor", joinColumns = @JoinColumn(name = "id_formalite_status"), inverseJoinColumns = @JoinColumn(name = "id_formalite_status_successor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<FormaliteStatus> successors;

	@OneToMany(targetEntity = FormaliteStatus.class)
	@JoinTable(name = "asso_formalite_status_predecessor", joinColumns = @JoinColumn(name = "id_formalite_status"), inverseJoinColumns = @JoinColumn(name = "id_formalite_status_predecessor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<FormaliteStatus> predecessors;

	private String aggregateStatus;

	private Integer servicePriority;

	public static String getFORMALITE_NEW() {
		return FORMALITE_NEW;
	}

	public static void setFORMALITE_NEW(String fORMALITE_NEW) {
		FORMALITE_NEW = fORMALITE_NEW;
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

	public List<FormaliteStatus> getSuccessors() {
		return successors;
	}

	public void setSuccessors(List<FormaliteStatus> successors) {
		this.successors = successors;
	}

	public List<FormaliteStatus> getPredecessors() {
		return predecessors;
	}

	public void setPredecessors(List<FormaliteStatus> predecessors) {
		this.predecessors = predecessors;
	}

	public String getAggregateStatus() {
		return aggregateStatus;
	}

	public void setAggregateStatus(String aggregateLabel) {
		this.aggregateStatus = aggregateLabel;
	}

	public static String getFORMALITE_IN_PROGRESS() {
		return FORMALITE_IN_PROGRESS;
	}

	public static void setFORMALITE_IN_PROGRESS(String fORMALITE_IN_PROGRESS) {
		FORMALITE_IN_PROGRESS = fORMALITE_IN_PROGRESS;
	}

	public static String getFORMALITE_WAITING_DOCUMENT() {
		return FORMALITE_WAITING_DOCUMENT;
	}

	public static void setFORMALITE_WAITING_DOCUMENT(String fORMALITE_WAITING_DOCUMENT) {
		FORMALITE_WAITING_DOCUMENT = fORMALITE_WAITING_DOCUMENT;
	}

	public static String getFORMALITE_WAITING_DOCUMENT_AUTHORITY() {
		return FORMALITE_WAITING_DOCUMENT_AUTHORITY;
	}

	public static void setFORMALITE_WAITING_DOCUMENT_AUTHORITY(String fORMALITE_WAITING_DOCUMENT_AUTHORITY) {
		FORMALITE_WAITING_DOCUMENT_AUTHORITY = fORMALITE_WAITING_DOCUMENT_AUTHORITY;
	}

	public static String getFORMALITE_AUTHORITY_REJECTED() {
		return FORMALITE_AUTHORITY_REJECTED;
	}

	public static void setFORMALITE_AUTHORITY_REJECTED(String fORMALITE_AUTHORITY_REJECTED) {
		FORMALITE_AUTHORITY_REJECTED = fORMALITE_AUTHORITY_REJECTED;
	}

	public static String getFORMALITE_AUTHORITY_VALIDATED() {
		return FORMALITE_AUTHORITY_VALIDATED;
	}

	public static void setFORMALITE_AUTHORITY_VALIDATED(String fORMALITE_AUTHORITY_VALIDATED) {
		FORMALITE_AUTHORITY_VALIDATED = fORMALITE_AUTHORITY_VALIDATED;
	}

	public static String getFORMALITE_DONE() {
		return FORMALITE_DONE;
	}

	public static void setFORMALITE_DONE(String fORMALITE_DONE) {
		FORMALITE_DONE = fORMALITE_DONE;
	}

	public Integer getServicePriority() {
		return servicePriority;
	}

	public void setServicePriority(Integer servicePriority) {
		this.servicePriority = servicePriority;
	}

}
