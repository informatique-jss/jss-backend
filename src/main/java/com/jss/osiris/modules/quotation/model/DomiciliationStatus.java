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
public class DomiciliationStatus implements Serializable, IId {

	/**
	 * WARNINNG : add update in DomiciliationStatutsService when adding a new status
	 */
	public static String DOMICILIATION_NEW = "DOMICILIATION_NEW";
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
	@JoinTable(name = "asso_domiciliation_status_successor", joinColumns = @JoinColumn(name = "id_domiciliation_status"), inverseJoinColumns = @JoinColumn(name = "id_domiciliation_status_successor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<DomiciliationStatus> successors;

	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name = "asso_domiciliation_status_predecessor", joinColumns = @JoinColumn(name = "id_domiciliation_status"), inverseJoinColumns = @JoinColumn(name = "id_domiciliation_status_predecessor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<DomiciliationStatus> predecessors;

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

	public void setSuccessors(List<DomiciliationStatus> successors) {
		this.successors = successors;
	}

	public List<DomiciliationStatus> getPredecessors() {
		return predecessors;
	}

	public void setPredecessors(List<DomiciliationStatus> predecessors) {
		this.predecessors = predecessors;
	}

}
