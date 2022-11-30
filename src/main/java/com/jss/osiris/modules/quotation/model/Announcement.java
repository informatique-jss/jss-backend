package com.jss.osiris.modules.quotation.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.IAttachment;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class Announcement implements IId, IAttachment {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "announcement_sequence")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_department")
	private Department department;

	@ManyToOne
	@JoinColumn(name = "id_confrere")
	private Confrere confrere;

	@ManyToOne
	@JoinColumn(name = "id_announcement_status")
	private AnnouncementStatus announcementStatus;

	@Column(nullable = false)
	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate publicationDate;

	@ManyToOne
	@JoinColumn(name = "id_notice_type_family")
	private NoticeTypeFamily noticeTypeFamily;

	@ManyToMany
	@JoinTable(name = "asso_announcement_notice_type", joinColumns = @JoinColumn(name = "id_announcement"), inverseJoinColumns = @JoinColumn(name = "id_notice_type"))
	private List<NoticeType> noticeTypes;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String notice;

	@Column(columnDefinition = "TEXT")
	private String noticeHeader;

	@Column(nullable = false)
	private Boolean isHeader;

	@Column(nullable = false)
	private Boolean isHeaderFree;

	@Column(nullable = false)
	private Boolean isProofReadingDocument;

	@OneToMany(mappedBy = "announcement")
	@JsonIgnoreProperties(value = { "announcement" }, allowSetters = true)
	private List<Attachment> attachments;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Confrere getConfrere() {
		return confrere;
	}

	public void setConfrere(Confrere confrere) {
		this.confrere = confrere;
	}

	public AnnouncementStatus getAnnouncementStatus() {
		return announcementStatus;
	}

	public void setAnnouncementStatus(AnnouncementStatus announcementStatus) {
		this.announcementStatus = announcementStatus;
	}

	public LocalDate getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(LocalDate publicationDate) {
		this.publicationDate = publicationDate;
	}

	public NoticeTypeFamily getNoticeTypeFamily() {
		return noticeTypeFamily;
	}

	public void setNoticeTypeFamily(NoticeTypeFamily noticeTypeFamily) {
		this.noticeTypeFamily = noticeTypeFamily;
	}

	public List<NoticeType> getNoticeTypes() {
		return noticeTypes;
	}

	public void setNoticeTypes(List<NoticeType> noticeTypes) {
		this.noticeTypes = noticeTypes;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public String getNoticeHeader() {
		return noticeHeader;
	}

	public void setNoticeHeader(String noticeHeader) {
		this.noticeHeader = noticeHeader;
	}

	public Boolean getIsHeader() {
		return isHeader;
	}

	public void setIsHeader(Boolean isHeader) {
		this.isHeader = isHeader;
	}

	public Boolean getIsHeaderFree() {
		return isHeaderFree;
	}

	public void setIsHeaderFree(Boolean isHeaderFree) {
		this.isHeaderFree = isHeaderFree;
	}

	public Boolean getIsProofReadingDocument() {
		return isProofReadingDocument;
	}

	public void setIsProofReadingDocument(Boolean isProofReadingDocument) {
		this.isProofReadingDocument = isProofReadingDocument;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

}
