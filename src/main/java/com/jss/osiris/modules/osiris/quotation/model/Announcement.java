package com.jss.osiris.modules.osiris.quotation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateSerializer;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.Department;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(indexes = { @Index(name = "idx_announcement_status", columnList = "id_announcement_status"),
		@Index(name = "idx_announcement_confrere", columnList = "id_confrere"),
		@Index(name = "idx_announcement_publication_date", columnList = "publication_date"),
})
public class Announcement implements IId {

	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class,
			JacksonViews.MyJssListView.class })
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_department")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class,
			JacksonViews.MyJssListView.class })
	private Department department;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_confrere")
	@JsonView({ JacksonViews.OsirisDetailedView.class })
	private Confrere confrere;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_announcement_status")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private AnnouncementStatus announcementStatus;

	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class,
			JacksonViews.MyJssListView.class })
	private LocalDate publicationDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_notice_type_family")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class,
			JacksonViews.MyJssListView.class })
	private NoticeTypeFamily noticeTypeFamily;

	@ManyToMany
	@JoinTable(name = "asso_announcement_notice_type", joinColumns = @JoinColumn(name = "id_announcement"), inverseJoinColumns = @JoinColumn(name = "id_notice_type"))
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class,
			JacksonViews.MyJssListView.class })
	private List<NoticeType> noticeTypes;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class })
	private String notice;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class })
	private String noticeHeader;

	@Column(nullable = false)
	@JsonView({ JacksonViews.MyJssDetailedView.class })
	private Boolean isHeader;

	@Column(nullable = false)
	@JsonView({ JacksonViews.MyJssDetailedView.class })
	private Boolean isHeaderFree;

	@Column(nullable = false)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class })
	private Boolean isProofReadingDocument;

	private Boolean isPublicationReciptAlreadySent;
	private Boolean isPublicationFlagAlreadySent;
	private Boolean isAnnouncementAlreadySentToConfrere;
	private Boolean isAnnouncementErratumAlreadySentToConfrere;

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

	@JsonView({ JacksonViews.OsirisDetailedView.class })
	private Integer characterNumber;

	private Boolean isBilanPublicationReminderIsSent;

	@OneToMany(mappedBy = "announcement")
	@JsonIgnore
	private List<Provision> provisions;

	@Transient
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class,
			JacksonViews.MyJssListView.class })
	private String affaireLabel;

	@Transient
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class,
			JacksonViews.MyJssListView.class })
	private String affaireSiren;

	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Boolean isReReadByJss;

	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Boolean isLegacy;

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

	public Boolean getIsAnnouncementErratumAlreadySentToConfrere() {
		return isAnnouncementErratumAlreadySentToConfrere;
	}

	public void setIsAnnouncementErratumAlreadySentToConfrere(Boolean isAnnouncementErratumAlreadySentToConfrere) {
		this.isAnnouncementErratumAlreadySentToConfrere = isAnnouncementErratumAlreadySentToConfrere;
	}

	public Boolean getIsBilanPublicationReminderIsSent() {
		return isBilanPublicationReminderIsSent;
	}

	public void setIsBilanPublicationReminderIsSent(Boolean isBilanPublicationReminderIsSent) {
		this.isBilanPublicationReminderIsSent = isBilanPublicationReminderIsSent;
	}

	public List<Provision> getProvisions() {
		return provisions;
	}

	public void setProvisions(List<Provision> provisions) {
		this.provisions = provisions;
	}

	public String getAffaireLabel() {
		return affaireLabel;
	}

	public void setAffaireLabel(String affaireLabel) {
		this.affaireLabel = affaireLabel;
	}

	public String getAffaireSiren() {
		return affaireSiren;
	}

	public void setAffaireSiren(String affaireSiren) {
		this.affaireSiren = affaireSiren;
	}

	public Boolean getIsReReadByJss() {
		return isReReadByJss;
	}

	public void setIsReReadByJss(Boolean isReReadByJss) {
		this.isReReadByJss = isReReadByJss;
	}

	public Boolean getIsLegacy() {
		return isLegacy;
	}

	public void setIsLegacy(Boolean isLegacy) {
		this.isLegacy = isLegacy;
	}

}
