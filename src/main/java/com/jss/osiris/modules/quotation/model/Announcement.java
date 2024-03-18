package com.jss.osiris.modules.quotation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.IDocument;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class Announcement implements IId, IDocument {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@IndexedField
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_department")
	private Department department;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_confrere")
	private Confrere confrere;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_announcement_status")
	private AnnouncementStatus announcementStatus;

	@Column(nullable = false)
	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate publicationDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_notice_type_family")
	private NoticeTypeFamily noticeTypeFamily;

	@ManyToMany
	@JoinTable(name = "asso_announcement_notice_type", joinColumns = @JoinColumn(name = "id_announcement"), inverseJoinColumns = @JoinColumn(name = "id_notice_type"))
	private List<NoticeType> noticeTypes;

	@Column(columnDefinition = "TEXT")
	private String notice;

	@Column(columnDefinition = "TEXT")
	private String noticeHeader;

	@Column(nullable = false)
	private Boolean isHeader;

	@Column(nullable = false)
	private Boolean isHeaderFree;

	@Column(nullable = false)
	private Boolean isProofReadingDocument;

	@OneToMany(targetEntity = Document.class, mappedBy = "announcement", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "announcement" }, allowSetters = true)
	private List<Document> documents;

	private Boolean isPublicationReciptAlreadySent;
	private Boolean isPublicationFlagAlreadySent;
	private Boolean isAnnouncementAlreadySentToConfrere;

	private LocalDateTime firstConfrereSentMailDateTime;
	private LocalDateTime firstConfrereReminderDateTime;
	private LocalDateTime secondConfrereReminderDateTime;
	private LocalDateTime thirdConfrereReminderDateTime;

	private LocalDateTime firstConfrereReminderProviderInvoiceDateTime;
	private LocalDateTime secondReminderProviderInvoiceDateTime;
	private LocalDateTime thirdReminderProviderInvoiceDateTime;

	private LocalDateTime firstClientReviewSentMailDateTime;
	private LocalDateTime firstClientReviewReminderDateTime;
	private LocalDateTime secondClientReviewReminderDateTime;
	private LocalDateTime thirdClientReviewReminderDateTime;

	private Integer actuLegaleId;

	private Boolean isComplexAnnouncement;

	private Integer characterNumber;

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

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public Boolean getIsPublicationReciptAlreadySent() {
		return isPublicationReciptAlreadySent;
	}

	public void setIsPublicationReciptAlreadySent(Boolean isPublicationReciptAlreadySent) {
		this.isPublicationReciptAlreadySent = isPublicationReciptAlreadySent;
	}

	public Boolean getIsPublicationFlagAlreadySent() {
		return isPublicationFlagAlreadySent;
	}

	public void setIsPublicationFlagAlreadySent(Boolean isPublicationFlagAlreadySent) {
		this.isPublicationFlagAlreadySent = isPublicationFlagAlreadySent;
	}

	public Integer getActuLegaleId() {
		return actuLegaleId;
	}

	public void setActuLegaleId(Integer actuLegaleId) {
		this.actuLegaleId = actuLegaleId;
	}

	public Boolean getIsAnnouncementAlreadySentToConfrere() {
		return isAnnouncementAlreadySentToConfrere;
	}

	public void setIsAnnouncementAlreadySentToConfrere(Boolean isAnnouncementAlreadySentToConfrere) {
		this.isAnnouncementAlreadySentToConfrere = isAnnouncementAlreadySentToConfrere;
	}

	public LocalDateTime getFirstConfrereReminderDateTime() {
		return firstConfrereReminderDateTime;
	}

	public void setFirstConfrereReminderDateTime(LocalDateTime firstConfrereReminderDateTime) {
		this.firstConfrereReminderDateTime = firstConfrereReminderDateTime;
	}

	public LocalDateTime getSecondConfrereReminderDateTime() {
		return secondConfrereReminderDateTime;
	}

	public void setSecondConfrereReminderDateTime(LocalDateTime secondConfrereReminderDateTime) {
		this.secondConfrereReminderDateTime = secondConfrereReminderDateTime;
	}

	public LocalDateTime getThirdConfrereReminderDateTime() {
		return thirdConfrereReminderDateTime;
	}

	public void setThirdConfrereReminderDateTime(LocalDateTime thirdConfrereReminderDateTime) {
		this.thirdConfrereReminderDateTime = thirdConfrereReminderDateTime;
	}

	public LocalDateTime getFirstConfrereSentMailDateTime() {
		return firstConfrereSentMailDateTime;
	}

	public void setFirstConfrereSentMailDateTime(LocalDateTime firstConfrereSentMailDateTime) {
		this.firstConfrereSentMailDateTime = firstConfrereSentMailDateTime;
	}

	public LocalDateTime getFirstClientReviewSentMailDateTime() {
		return firstClientReviewSentMailDateTime;
	}

	public void setFirstClientReviewSentMailDateTime(LocalDateTime firstClientReviewSentMailDateTime) {
		this.firstClientReviewSentMailDateTime = firstClientReviewSentMailDateTime;
	}

	public LocalDateTime getFirstClientReviewReminderDateTime() {
		return firstClientReviewReminderDateTime;
	}

	public void setFirstClientReviewReminderDateTime(LocalDateTime firstClientReviewReminderDateTime) {
		this.firstClientReviewReminderDateTime = firstClientReviewReminderDateTime;
	}

	public LocalDateTime getSecondClientReviewReminderDateTime() {
		return secondClientReviewReminderDateTime;
	}

	public void setSecondClientReviewReminderDateTime(LocalDateTime secondClientReviewReminderDateTime) {
		this.secondClientReviewReminderDateTime = secondClientReviewReminderDateTime;
	}

	public LocalDateTime getThirdClientReviewReminderDateTime() {
		return thirdClientReviewReminderDateTime;
	}

	public void setThirdClientReviewReminderDateTime(LocalDateTime thirdClientReviewReminderDateTime) {
		this.thirdClientReviewReminderDateTime = thirdClientReviewReminderDateTime;
	}

	public Boolean getIsComplexAnnouncement() {
		return isComplexAnnouncement;
	}

	public void setIsComplexAnnouncement(Boolean isComplexAnnouncement) {
		this.isComplexAnnouncement = isComplexAnnouncement;
	}

	public Integer getCharacterNumber() {
		return characterNumber;
	}

	public void setCharacterNumber(Integer characterNumber) {
		this.characterNumber = characterNumber;
	}

	public LocalDateTime getFirstConfrereReminderProviderInvoiceDateTime() {
		return firstConfrereReminderProviderInvoiceDateTime;
	}

	public void setFirstConfrereReminderProviderInvoiceDateTime(
			LocalDateTime firstConfrereReminderProviderInvoiceDateTime) {
		this.firstConfrereReminderProviderInvoiceDateTime = firstConfrereReminderProviderInvoiceDateTime;
	}

	public LocalDateTime getSecondReminderProviderInvoiceDateTime() {
		return secondReminderProviderInvoiceDateTime;
	}

	public void setSecondReminderProviderInvoiceDateTime(LocalDateTime secondReminderProviderInvoiceDateTime) {
		this.secondReminderProviderInvoiceDateTime = secondReminderProviderInvoiceDateTime;
	}

	public LocalDateTime getThirdReminderProviderInvoiceDateTime() {
		return thirdReminderProviderInvoiceDateTime;
	}

	public void setThirdReminderProviderInvoiceDateTime(LocalDateTime thirdReminderProviderInvoiceDateTime) {
		this.thirdReminderProviderInvoiceDateTime = thirdReminderProviderInvoiceDateTime;
	}

}
