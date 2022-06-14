package com.jss.jssbackend.modules.quotation.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jss.jssbackend.modules.miscellaneous.model.Department;
import com.jss.jssbackend.modules.miscellaneous.model.IId;

@Entity
public class Shal implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_provision")
	@JsonBackReference("provision")
	private Provision provision;

	@ManyToOne
	@JoinColumn(name = "id_department")
	private Department department;

	@ManyToOne
	@JoinColumn(name = "id_confrere")
	private Confrere confrere;

	@ManyToOne
	@JoinColumn(name = "id_journal_type")
	private JournalType journalType;

	@Column(nullable = false)
	private Date publicationDate;

	@Column(nullable = false)
	private Boolean isRedactedByJss;

	@ManyToOne
	@JoinColumn(name = "id_notice_type_family")
	private NoticeTypeFamily noticeTypeFamily;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_shal_notice_type", joinColumns = @JoinColumn(name = "id_shal"), inverseJoinColumns = @JoinColumn(name = "id_notice_type"))
	private List<NoticeType> noticeTypes;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String notice;

	@Column(nullable = false)
	private Boolean isLogo;

	@Column(nullable = false)
	private Boolean isHeader;

	@Column(nullable = false)
	private Boolean isPictureBaloPackage;

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

	public Integer getId() {
		return id;
	}

	public NoticeTypeFamily getNoticeTypeFamily() {
		return noticeTypeFamily;
	}

	public Boolean getIsProofReadingDocument() {
		return isProofReadingDocument;
	}

	public void setIsProofReadingDocument(Boolean isProofReadingDocument) {
		this.isProofReadingDocument = isProofReadingDocument;
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

	public Boolean getIsLogo() {
		return isLogo;
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

	public void setIsLogo(Boolean isLogo) {
		this.isLogo = isLogo;
	}

	public Boolean getIsHeader() {
		return isHeader;
	}

	public void setIsHeader(Boolean isHeader) {
		this.isHeader = isHeader;
	}

	public Boolean getIsPictureBaloPackage() {
		return isPictureBaloPackage;
	}

	public void setIsPictureBaloPackage(Boolean isPictureBaloPackage) {
		this.isPictureBaloPackage = isPictureBaloPackage;
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

	public Provision getProvision() {
		return provision;
	}

	public Date getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
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

	public JournalType getJournalType() {
		return journalType;
	}

	public void setJournalType(JournalType journalType) {
		this.journalType = journalType;
	}

}
