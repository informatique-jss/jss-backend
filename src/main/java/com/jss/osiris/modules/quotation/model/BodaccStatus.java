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
public class BodaccStatus implements Serializable, IId {
	/**
	 * WARNINNG : add update in BodaccStatutsService when adding a new status
	 */
	public static String BODACC_NEW = "BODACC_NEW";
	public static String BODACC_IN_PROGRESS = "BODACC_IN_PROGRESS";
	public static String BODACC_WAITING_DOCUMENT = "BODACC_WAITING_DOCUMENT";
	public static String BODACC_WAITING_DOCUMENT_BODACC = "BODACC_WAITING_DOCUMENT_BODACC";
	public static String BODACC_WAITING_PUBLICATION = "BODACC_WAITING_PUBLICATION";
	public static String BODACC_PUBLISHED = "BODACC_PUBLISHED";
	public static String BODACC_DONE = "BODACC_DONE";
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
	@JoinTable(name = "asso_bodacc_status_successor", joinColumns = @JoinColumn(name = "id_bodacc_status"), inverseJoinColumns = @JoinColumn(name = "id_bodacc_status_successor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<BodaccStatus> successors;

	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name = "asso_bodacc_status_predecessor", joinColumns = @JoinColumn(name = "id_bodacc_status"), inverseJoinColumns = @JoinColumn(name = "id_bodacc_status_predecessor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<BodaccStatus> predecessors;

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

	public List<BodaccStatus> getSuccessors() {
		return successors;
	}

	public void setSuccessors(List<BodaccStatus> successors) {
		this.successors = successors;
	}

	public List<BodaccStatus> getPredecessors() {
		return predecessors;
	}

	public void setPredecessors(List<BodaccStatus> predecessors) {
		this.predecessors = predecessors;
	}

}
