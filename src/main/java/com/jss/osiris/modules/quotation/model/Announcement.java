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

	@Column(nullable = false)
	private Boolean isRedactedByJss;

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
	private Boolean isLegalDisplay;

	private Float posterProductionPrice;
	private Float posterProductionJSSPrice;
	private Float billPostingPrice;
	private Float billPostingJSSPrice;
	private Float bailiffReportPrice;
	private Float bailiffReportJSSPrice;

	@Column(nullable = false)
	private Boolean isProofReadingDocument;

	@Column(nullable = false)
	private Boolean isPublicationCertificateDocument;

	@OneToMany(mappedBy = "announcement")
	@JsonIgnoreProperties(value = { "announcement" }, allowSetters = true)
	private List<Attachment> attachments;

	public Integer getId() {
		return id;
	}

	public NoticeTypeFamily getNoticeTypeFamily() {
		return noticeTypeFamily;
	}

	public Boolean getIsProofReadingDocument() {
		return isProofReadingDocument;
	}

	public String getNoticeHeader() {
		return noticeHeader;
	}

	public void setNoticeHeader(String noticeHeader) {
		this.noticeHeader = noticeHeader;
	}

	public void setIsProofReadingDocument(Boolean isProofReadingDocument) {
		this.isProofReadingDocument = isProofReadingDocument;
	}

	public Boolean getIsHeaderFree() {
		return isHeaderFree;
	}

	public void setIsHeaderFree(Boolean isHeaderFree) {
		this.isHeaderFree = isHeaderFree;
	}

	public Boolean getIsPublicationCertificateDocument() {
		return isPublicationCertificateDocument;
	}

	public void setIsPublicationCertificateDocument(Boolean isPublicationCertificateDocument) {
		this.isPublicationCertificateDocument = isPublicationCertificateDocument;
	}

	public void setNoticeTypeFamily(NoticeTypeFamily noticeTypeFamily) {
		this.noticeTypeFamily = noticeTypeFamily;
	}

	public Boolean getIsLegalDisplay() {
		return isLegalDisplay;
	}

	public void setIsLegalDisplay(Boolean isLegalDisplay) {
		this.isLegalDisplay = isLegalDisplay;
	}

	public Float getPosterProductionPrice() {
		return posterProductionPrice;
	}

	public void setPosterProductionPrice(Float posterProductionPrice) {
		this.posterProductionPrice = posterProductionPrice;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public Float getPosterProductionJSSPrice() {
		return posterProductionJSSPrice;
	}

	public void setPosterProductionJSSPrice(Float posterProductionJSSPrice) {
		this.posterProductionJSSPrice = posterProductionJSSPrice;
	}

	public Float getBillPostingPrice() {
		return billPostingPrice;
	}

	public void setBillPostingPrice(Float billPostingPrice) {
		this.billPostingPrice = billPostingPrice;
	}

	public Float getBillPostingJSSPrice() {
		return billPostingJSSPrice;
	}

	public void setBillPostingJSSPrice(Float billPostingJSSPrice) {
		this.billPostingJSSPrice = billPostingJSSPrice;
	}

	public Float getBailiffReportPrice() {
		return bailiffReportPrice;
	}

	public void setBailiffReportPrice(Float bailiffReportPrice) {
		this.bailiffReportPrice = bailiffReportPrice;
	}

	public Float getBailiffReportJSSPrice() {
		return bailiffReportJSSPrice;
	}

	public void setBailiffReportJSSPrice(Float bailiffReportJSSPrice) {
		this.bailiffReportJSSPrice = bailiffReportJSSPrice;
	}

	public Boolean getIsHeader() {
		return isHeader;
	}

	public void setIsHeader(Boolean isHeader) {
		this.isHeader = isHeader;
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

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getIsRedactedByJss() {
		return isRedactedByJss;
	}

	public void setIsRedactedByJss(Boolean isRedactedByJss) {
		this.isRedactedByJss = isRedactedByJss;
	}

	public LocalDate getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(LocalDate publicationDate) {
		this.publicationDate = publicationDate;
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

}
