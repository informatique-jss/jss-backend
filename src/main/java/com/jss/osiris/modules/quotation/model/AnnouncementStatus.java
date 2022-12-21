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
public class AnnouncementStatus implements Serializable, IId {

	/**
	 * WARNINNG : add update in AnnouncementStatutsService when adding a new status
	 */
	public static String ANNOUNCEMENT_NEW = "ANNOUNCEMENT_NEW";
	public static String ANNOUNCEMENT_IN_PROGRESS = "ANNOUNCEMENT_IN_PROGRESS";
	public static String ANNOUNCEMENT_WAITING_DOCUMENT = "ANNOUNCEMENT_WAITING_DOCUMENT";
	public static String ANNOUNCEMENT_WAITING_CONFRERE = "ANNOUNCEMENT_WAITING_CONFRERE";
	public static String ANNOUNCEMENT_WAITING_CONFRERE_PUBLISHED = "ANNOUNCEMENT_WAITING_CONFRERE_PUBLISHED";
	public static String ANNOUNCEMENT_DONE = "ANNOUNCEMENT_DONE";
	public static String ANNOUNCEMENT_WAITING_READ = "ANNOUNCEMENT_WAITING_READ";
	public static String ANNOUNCEMENT_PUBLISHED = "ANNOUNCEMENT_PUBLISHED";

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
	@JoinTable(name = "asso_announcement_status_successor", joinColumns = @JoinColumn(name = "id_announcement_status"), inverseJoinColumns = @JoinColumn(name = "id_announcement_status_successor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<AnnouncementStatus> successors;

	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name = "asso_announcement_status_predecessor", joinColumns = @JoinColumn(name = "id_announcement_status"), inverseJoinColumns = @JoinColumn(name = "id_announcement_status_predecessor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<AnnouncementStatus> predecessors;

	public static String getANNOUNCEMENT_NEW() {
		return ANNOUNCEMENT_NEW;
	}

	public static void setANNOUNCEMENT_NEW(String aNNOUNCEMENT_NEW) {
		ANNOUNCEMENT_NEW = aNNOUNCEMENT_NEW;
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

}
