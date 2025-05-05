package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;

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
public class DomiciliationStatus extends IWorkflowElement implements Serializable {

	/**
	 * WARNINNG : add update in DomiciliationStatutsService when adding a new status
	 */
	public static String DOMICILIATION_NEW = "DOMICILIATION_NEW";
	public static String DOMICILIATION_IN_PROGRESS = "DOMICILIATION_IN_PROGRESS";
	public static String DOMICILIATION_WAITING_FOR_DOCUMENTS = "DOMICILIATION_WAITING_FOR_DOCUMENTS";
	public static String DOMICILIATION_DONE = "DOMICILIATION_DONE";
	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Integer id;

	@Column(nullable = false, length = 100)
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private String label;

	@Column(nullable = false, length = 100)
	private String code;

	private String icon;

	private Boolean isOpenState;
	private Boolean isCloseState;

	@OneToMany(targetEntity = DomiciliationStatus.class)
	@JoinTable(name = "asso_domiciliation_status_successor", joinColumns = @JoinColumn(name = "id_domiciliation_status"), inverseJoinColumns = @JoinColumn(name = "id_domiciliation_status_successor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<DomiciliationStatus> successors;

	@OneToMany(targetEntity = DomiciliationStatus.class)
	@JoinTable(name = "asso_domiciliation_status_predecessor", joinColumns = @JoinColumn(name = "id_domiciliation_status"), inverseJoinColumns = @JoinColumn(name = "id_domiciliation_status_predecessor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<DomiciliationStatus> predecessors;

	private String aggregateStatus;

	private Integer servicePriority;

	public static String getDOMICILIATION_NEW() {
		return DOMICILIATION_NEW;
	}

	public static void setDOMICILIATION_NEW(String dOMICILIATION_NEW) {
		DOMICILIATION_NEW = dOMICILIATION_NEW;
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

	public List<DomiciliationStatus> getSuccessors() {
		return successors;
	}

	public List<DomiciliationStatus> getPredecessors() {
		return predecessors;
	}

	public String getAggregateStatus() {
		return aggregateStatus;
	}

	public void setAggregateStatus(String aggregateLabel) {
		this.aggregateStatus = aggregateLabel;
	}

	public static String getDOMICILIATION_IN_PROGRESS() {
		return DOMICILIATION_IN_PROGRESS;
	}

	public static void setDOMICILIATION_IN_PROGRESS(String dOMICILIATION_IN_PROGRESS) {
		DOMICILIATION_IN_PROGRESS = dOMICILIATION_IN_PROGRESS;
	}

	public static String getDOMICILIATION_WAITING_FOR_DOCUMENTS() {
		return DOMICILIATION_WAITING_FOR_DOCUMENTS;
	}

	public static void setDOMICILIATION_WAITING_FOR_DOCUMENTS(String dOMICILIATION_WAITING_FOR_DOCUMENTS) {
		DOMICILIATION_WAITING_FOR_DOCUMENTS = dOMICILIATION_WAITING_FOR_DOCUMENTS;
	}

	public static String getDOMICILIATION_DONE() {
		return DOMICILIATION_DONE;
	}

	public static void setDOMICILIATION_DONE(String dOMICILIATION_DONE) {
		DOMICILIATION_DONE = dOMICILIATION_DONE;
	}

	public Integer getServicePriority() {
		return servicePriority;
	}

	public void setServicePriority(Integer servicePriority) {
		this.servicePriority = servicePriority;
	}

}
