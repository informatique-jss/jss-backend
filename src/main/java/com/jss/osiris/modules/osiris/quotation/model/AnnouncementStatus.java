package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
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
	@JsonView({ JacksonViews.OsirisDetailedView.class })
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

	private Integer servicePriority;

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

	public static String getANNOUNCEMENT_NEW() {
		return ANNOUNCEMENT_NEW;
	}

	public static void setANNOUNCEMENT_NEW(String aNNOUNCEMENT_NEW) {
		ANNOUNCEMENT_NEW = aNNOUNCEMENT_NEW;
	}

	public static String getANNOUNCEMENT_IN_PROGRESS() {
		return ANNOUNCEMENT_IN_PROGRESS;
	}

	public static void setANNOUNCEMENT_IN_PROGRESS(String aNNOUNCEMENT_IN_PROGRESS) {
		ANNOUNCEMENT_IN_PROGRESS = aNNOUNCEMENT_IN_PROGRESS;
	}

	public static String getANNOUNCEMENT_WAITING_DOCUMENT() {
		return ANNOUNCEMENT_WAITING_DOCUMENT;
	}

	public static void setANNOUNCEMENT_WAITING_DOCUMENT(String aNNOUNCEMENT_WAITING_DOCUMENT) {
		ANNOUNCEMENT_WAITING_DOCUMENT = aNNOUNCEMENT_WAITING_DOCUMENT;
	}

	public static String getANNOUNCEMENT_WAITING_CONFRERE() {
		return ANNOUNCEMENT_WAITING_CONFRERE;
	}

	public static void setANNOUNCEMENT_WAITING_CONFRERE(String aNNOUNCEMENT_WAITING_CONFRERE) {
		ANNOUNCEMENT_WAITING_CONFRERE = aNNOUNCEMENT_WAITING_CONFRERE;
	}

	public static String getANNOUNCEMENT_WAITING_CONFRERE_PUBLISHED() {
		return ANNOUNCEMENT_WAITING_CONFRERE_PUBLISHED;
	}

	public static void setANNOUNCEMENT_WAITING_CONFRERE_PUBLISHED(String aNNOUNCEMENT_WAITING_CONFRERE_PUBLISHED) {
		ANNOUNCEMENT_WAITING_CONFRERE_PUBLISHED = aNNOUNCEMENT_WAITING_CONFRERE_PUBLISHED;
	}

	public static String getANNOUNCEMENT_DONE() {
		return ANNOUNCEMENT_DONE;
	}

	public static void setANNOUNCEMENT_DONE(String aNNOUNCEMENT_DONE) {
		ANNOUNCEMENT_DONE = aNNOUNCEMENT_DONE;
	}

	public static String getANNOUNCEMENT_WAITING_READ_CUSTOMER() {
		return ANNOUNCEMENT_WAITING_READ_CUSTOMER;
	}

	public static void setANNOUNCEMENT_WAITING_READ_CUSTOMER(String aNNOUNCEMENT_WAITING_READ_CUSTOMER) {
		ANNOUNCEMENT_WAITING_READ_CUSTOMER = aNNOUNCEMENT_WAITING_READ_CUSTOMER;
	}

	public static String getANNOUNCEMENT_WAITING_CONFRERE_INVOICE() {
		return ANNOUNCEMENT_WAITING_CONFRERE_INVOICE;
	}

	public static void setANNOUNCEMENT_WAITING_CONFRERE_INVOICE(String aNNOUNCEMENT_WAITING_CONFRERE_INVOICE) {
		ANNOUNCEMENT_WAITING_CONFRERE_INVOICE = aNNOUNCEMENT_WAITING_CONFRERE_INVOICE;
	}

	public static String getANNOUNCEMENT_PUBLISHED() {
		return ANNOUNCEMENT_PUBLISHED;
	}

	public static void setANNOUNCEMENT_PUBLISHED(String aNNOUNCEMENT_PUBLISHED) {
		ANNOUNCEMENT_PUBLISHED = aNNOUNCEMENT_PUBLISHED;
	}

	public Integer getServicePriority() {
		return servicePriority;
	}

	public void setServicePriority(Integer servicePriority) {
		this.servicePriority = servicePriority;
	}

}
