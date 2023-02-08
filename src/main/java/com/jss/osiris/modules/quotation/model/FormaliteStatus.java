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
public class FormaliteStatus implements Serializable, IId {

	/**
	 * WARNINNG : add update in FormaliteStatutsService when adding a new status
	 */
	public static String FORMALITE_NEW = "FORMALITE_NEW";
	public static String FORMALITE_IN_PROGRESS = "FORMALITE_IN_PROGRESS";
	public static String FORMALITE_WAITING_DOCUMENT = "FORMALITE_WAITING_DOCUMENT";
	public static String FORMALITE_SENT = "FORMALITE_SENT";
	public static String FORMALITE_REFUSED = "FORMALITE_REFUSED";
	public static String FORMALITE_WAITING_DOCUMENT_GREFFE = "FORMALITE_WAITING_DOCUMENT_GREFFE";
	public static String FORMALITE_VALIDATE = "FORMALITE_VALIDATE";
	public static String FORMALITE_DONE = "FORMALITE_DONE";
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
	@JoinTable(name = "asso_formalite_status_successor", joinColumns = @JoinColumn(name = "id_formalite_status"), inverseJoinColumns = @JoinColumn(name = "id_formalite_status_successor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<FormaliteStatus> successors;

	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name = "asso_formalite_status_predecessor", joinColumns = @JoinColumn(name = "id_formalite_status"), inverseJoinColumns = @JoinColumn(name = "id_formalite_status_predecessor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<FormaliteStatus> predecessors;

	private String aggregateLabel;

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

	public String getAggregateLabel() {
		return aggregateLabel;
	}

	public void setAggregateLabel(String aggregateLabel) {
		this.aggregateLabel = aggregateLabel;
	}

}
