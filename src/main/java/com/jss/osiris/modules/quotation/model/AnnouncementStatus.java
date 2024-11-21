package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;

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
public class AnnouncementStatus implements Serializable, IId {

	/**
	 * WARNINNG : add update in AnnouncementStatutsService when adding a new status
	 */
	public static String ANNOUNCEMENT_NEW = "ANNOUNCEMENT_NEW";
	public static String ANNOUNCEMENT_IN_PROGRESS = "ANNOUNCEMENT_IN_PROGRESS";
	public static String ANNOUNCEMENT_WAITING_DOCUMENT = "ANNOUNCEMENT_WAITING_DOCUMENT";
	public static String ANNOUNCEMENT_WAITING_CONFRERE = "ANNOUNCEMENT_WAITING_CONFRERE";
	public static String ANNOUNCEMENT_WAITING_LINKED_PROVISION = "ANNOUNCEMENT_WAITING_LINKED_PROVISION";
	public static String ANNOUNCEMENT_WAITING_CONFRERE_PUBLISHED = "ANNOUNCEMENT_WAITING_CONFRERE_PUBLISHED";
	public static String ANNOUNCEMENT_DONE = "ANNOUNCEMENT_DONE";
	public static String ANNOUNCEMENT_WAITING_READ_CUSTOMER = "ANNOUNCEMENT_WAITING_READ_CUSTOMER";
	public static String ANNOUNCEMENT_WAITING_CONFRERE_INVOICE = "ANNOUNCEMENT_WAITING_CONFRERE_INVOICE";
	public static String ANNOUNCEMENT_PUBLISHED = "ANNOUNCEMENT_PUBLISHED";

	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	private Integer id;

	@Column(nullable = false, length = 100)
	private String label;

	@Column(nullable = false, length = 100)
	private String code;

	private String icon;

	private Boolean isOpenState;
	private Boolean isCloseState;

	@OneToMany(targetEntity = AnnouncementStatus.class)
	@JoinTable(name = "asso_announcement_status_successor", joinColumns = @JoinColumn(name = "id_announcement_status"), inverseJoinColumns = @JoinColumn(name = "id_announcement_status_successor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<AnnouncementStatus> successors;

	@OneToMany(targetEntity = AnnouncementStatus.class)
	@JoinTable(name = "asso_announcement_status_predecessor", joinColumns = @JoinColumn(name = "id_announcement_status"), inverseJoinColumns = @JoinColumn(name = "id_announcement_status_predecessor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<AnnouncementStatus> predecessors;

	private String aggregateStatus;

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

	public List<AnnouncementStatus> getSuccessors() {
		return successors;
	}

	public void setSuccessors(List<AnnouncementStatus> successors) {
		this.successors = successors;
	}

	public List<AnnouncementStatus> getPredecessors() {
		return predecessors;
	}

	public void setPredecessors(List<AnnouncementStatus> predecessors) {
		this.predecessors = predecessors;
	}

	public String getAggregateStatus() {
		return aggregateStatus;
	}

	public void setAggregateStatus(String aggregateStatus) {
		this.aggregateStatus = aggregateStatus;
	}

}
