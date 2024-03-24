package com.jss.osiris.modules.quotation.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.IAttachment;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.profile.model.Employee;

@Entity
@Table(indexes = { @Index(name = "idx_provision_service", columnList = "id_service"),
		@Index(name = "idx_provision_formalite", columnList = "id_formalite"),
		@Index(name = "idx_provision_domicialitation", columnList = "id_domiciliation"),
		@Index(name = "idx_provision_simple_provision", columnList = "id_simple_provision"),
		@Index(name = "idx_provision_announcement", columnList = "id_announcement"),
})
public class Provision implements IId, IAttachment {

	@Id
	@SequenceGenerator(name = "provision_sequence", sequenceName = "provision_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "provision_sequence")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_service")
	@JsonIgnoreProperties(value = { "provisions" }, allowSetters = true)
	private Service service;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "defaultCompetentAuthorityServiceProvider" }, allowSetters = true)
	@JoinColumn(name = "id_provision_type")
	@IndexedField
	private ProvisionType provisionType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provision_family_type")
	@IndexedField
	private ProvisionFamilyType provisionFamilyType;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_domiciliation")
	private Domiciliation domiciliation;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_announcement")
	@IndexedField
	private Announcement announcement;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_simple_provision")
	@IndexedField
	private SimpleProvision simpleProvision;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "provision" }, allowSetters = true)
	@JoinColumn(name = "id_formalite")
	@IndexedField
	private Formalite formalite;

	@OneToMany(targetEntity = InvoiceItem.class, mappedBy = "provision", cascade = CascadeType.REMOVE)
	@JsonIgnoreProperties(value = { "provision", "originProviderInvoice" }, allowSetters = true)
	private List<InvoiceItem> invoiceItems;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_employee")
	@IndexedField
	private Employee assignedTo;

	@Column(nullable = false)
	private Boolean isLogo;

	@Column(nullable = false)
	private Boolean isRedactedByJss;

	@Column(nullable = false)
	private Boolean isBaloPackage;

	private Boolean isBaloPublicationFlag;
	private Boolean isBaloNormalization;

	@Column(nullable = false)
	private Boolean isPublicationPaper;

	private Integer publicationPaperAffaireNumber;
	private Integer publicationPaperClientNumber;

	@Column(nullable = false)
	private Boolean isPublicationReceipt;

	@Column(nullable = false)
	private Boolean isPublicationFlag;

	@Column(nullable = false)
	private Boolean isBodaccFollowup;

	@Column(nullable = false)
	private Boolean isBodaccFollowupAndRedaction;

	@Column(nullable = false)
	private Boolean isNantissementDeposit;

	@Column(nullable = false)
	private Boolean isSocialShareNantissementRedaction;

	@Column(nullable = false)
	private Boolean isBusinnessNantissementRedaction;

	@Column(nullable = false)
	private Boolean isSellerPrivilegeRedaction;

	@Column(nullable = false)
	private Boolean isTreatmentMultipleModiciation;

	@Column(nullable = false)
	private Boolean isVacationMultipleModification;

	@Column(nullable = false)
	private Boolean isRegisterPurchase;

	@Column(nullable = false)
	private Boolean isRegisterInitials;

	@Column(nullable = false)
	private Boolean isRegisterShippingCosts;

	@Column(nullable = false)
	private Boolean isDisbursement;

	@Column(nullable = false)
	private Boolean isFeasibilityStudy;

	@Column(nullable = false)
	private Boolean isChronopostFees;

	@Column(nullable = false)
	private Boolean isApplicationFees;

	@Column(nullable = false)
	private Boolean isBankCheque;

	@Column(nullable = false)
	private Boolean isComplexeFile;

	@Column(nullable = false)
	private Boolean isBilan;

	@Column(nullable = false)
	private Boolean isDocumentScanning;

	@Column(nullable = false)
	private Boolean isEmergency;

	@Column(nullable = false)
	private Boolean isRneUpdate;

	@Column(nullable = false)
	private Boolean isVacationUpdateBeneficialOwners;

	@Column(nullable = false)
	private Boolean isFormalityAdditionalDeclaration;

	@Column(nullable = false)
	private Boolean isCorrespondenceFees;

	@OneToMany(targetEntity = Attachment.class, mappedBy = "provision", cascade = CascadeType.REMOVE)
	@JsonIgnoreProperties(value = { "provision", "invoice" }, allowSetters = true)
	private List<Attachment> attachments;

	@OneToMany(targetEntity = Invoice.class, mappedBy = "provision")
	@JsonIgnoreProperties(value = { "provision", "customerOrder", "accountingRecords", "payments", "invoice",
			"originPayment",
			"childrenPayments", "customerOrderForInboundInvoice" }, allowSetters = true)
	private List<Invoice> providerInvoices;

	@OneToMany(targetEntity = Payment.class, mappedBy = "provision")
	@JsonIgnoreProperties(value = { "provision", "accountingRecords", "assoAffaireOrder", "customerOrder",
			"childrenPayments",
			"invoice" }, allowSetters = true)
	private List<Payment> payments;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ProvisionType getProvisionType() {
		return provisionType;
	}

	public void setProvisionType(ProvisionType provisionType) {
		this.provisionType = provisionType;
	}

	public ProvisionFamilyType getProvisionFamilyType() {
		return provisionFamilyType;
	}

	public void setProvisionFamilyType(ProvisionFamilyType provisionFamilyType) {
		this.provisionFamilyType = provisionFamilyType;
	}

	public Domiciliation getDomiciliation() {
		return domiciliation;
	}

	public void setDomiciliation(Domiciliation domiciliation) {
		this.domiciliation = domiciliation;
	}

	public Announcement getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(Announcement announcement) {
		this.announcement = announcement;
	}

	public List<InvoiceItem> getInvoiceItems() {
		return invoiceItems;
	}

	public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
		this.invoiceItems = invoiceItems;
	}

	public Employee getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(Employee assignedTo) {
		this.assignedTo = assignedTo;
	}

	public Boolean getIsLogo() {
		return isLogo;
	}

	public void setIsLogo(Boolean isLogo) {
		this.isLogo = isLogo;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public Formalite getFormalite() {
		return formalite;
	}

	public void setFormalite(Formalite formalite) {
		this.formalite = formalite;
	}

	public Boolean getIsRedactedByJss() {
		return isRedactedByJss;
	}

	public void setIsRedactedByJss(Boolean isRedactedByJss) {
		this.isRedactedByJss = isRedactedByJss;
	}

	public Boolean getIsBaloPackage() {
		return isBaloPackage;
	}

	public void setIsBaloPackage(Boolean isBaloPackage) {
		this.isBaloPackage = isBaloPackage;
	}

	public Boolean getIsPublicationReceipt() {
		return isPublicationReceipt;
	}

	public void setIsPublicationReceipt(Boolean isPublicationReceipt) {
		this.isPublicationReceipt = isPublicationReceipt;
	}

	public Boolean getIsPublicationFlag() {
		return isPublicationFlag;
	}

	public void setIsPublicationFlag(Boolean isPublicationFlag) {
		this.isPublicationFlag = isPublicationFlag;
	}

	public Boolean getIsBodaccFollowup() {
		return isBodaccFollowup;
	}

	public void setIsBodaccFollowup(Boolean isBodaccFollowup) {
		this.isBodaccFollowup = isBodaccFollowup;
	}

	public Boolean getIsBodaccFollowupAndRedaction() {
		return isBodaccFollowupAndRedaction;
	}

	public void setIsBodaccFollowupAndRedaction(Boolean isBodaccFollowupAndRedaction) {
		this.isBodaccFollowupAndRedaction = isBodaccFollowupAndRedaction;
	}

	public Boolean getIsNantissementDeposit() {
		return isNantissementDeposit;
	}

	public void setIsNantissementDeposit(Boolean isNantissementDeposit) {
		this.isNantissementDeposit = isNantissementDeposit;
	}

	public Boolean getIsSocialShareNantissementRedaction() {
		return isSocialShareNantissementRedaction;
	}

	public void setIsSocialShareNantissementRedaction(Boolean isSocialShareNantissementRedaction) {
		this.isSocialShareNantissementRedaction = isSocialShareNantissementRedaction;
	}

	public Boolean getIsBusinnessNantissementRedaction() {
		return isBusinnessNantissementRedaction;
	}

	public void setIsBusinnessNantissementRedaction(Boolean isBusinnessNantissementRedaction) {
		this.isBusinnessNantissementRedaction = isBusinnessNantissementRedaction;
	}

	public Boolean getIsSellerPrivilegeRedaction() {
		return isSellerPrivilegeRedaction;
	}

	public void setIsSellerPrivilegeRedaction(Boolean isSellerPrivilegeRedaction) {
		this.isSellerPrivilegeRedaction = isSellerPrivilegeRedaction;
	}

	public Boolean getIsTreatmentMultipleModiciation() {
		return isTreatmentMultipleModiciation;
	}

	public void setIsTreatmentMultipleModiciation(Boolean isTreatmentMultipleModiciation) {
		this.isTreatmentMultipleModiciation = isTreatmentMultipleModiciation;
	}

	public Boolean getIsVacationMultipleModification() {
		return isVacationMultipleModification;
	}

	public void setIsVacationMultipleModification(Boolean isVacationMultipleModification) {
		this.isVacationMultipleModification = isVacationMultipleModification;
	}

	public Boolean getIsRegisterPurchase() {
		return isRegisterPurchase;
	}

	public void setIsRegisterPurchase(Boolean isRegisterPurchase) {
		this.isRegisterPurchase = isRegisterPurchase;
	}

	public Boolean getIsRegisterInitials() {
		return isRegisterInitials;
	}

	public void setIsRegisterInitials(Boolean isRegisterInitials) {
		this.isRegisterInitials = isRegisterInitials;
	}

	public Boolean getIsRegisterShippingCosts() {
		return isRegisterShippingCosts;
	}

	public void setIsRegisterShippingCosts(Boolean isRegisterShippingCosts) {
		this.isRegisterShippingCosts = isRegisterShippingCosts;
	}

	public Boolean getIsDisbursement() {
		return isDisbursement;
	}

	public void setIsDisbursement(Boolean isDisbursement) {
		this.isDisbursement = isDisbursement;
	}

	public Boolean getIsFeasibilityStudy() {
		return isFeasibilityStudy;
	}

	public void setIsFeasibilityStudy(Boolean isFeasibilityStudy) {
		this.isFeasibilityStudy = isFeasibilityStudy;
	}

	public Boolean getIsChronopostFees() {
		return isChronopostFees;
	}

	public void setIsChronopostFees(Boolean isChronopostFees) {
		this.isChronopostFees = isChronopostFees;
	}

	public Boolean getIsBankCheque() {
		return isBankCheque;
	}

	public void setIsBankCheque(Boolean isBankCheque) {
		this.isBankCheque = isBankCheque;
	}

	public Boolean getIsComplexeFile() {
		return isComplexeFile;
	}

	public void setIsComplexeFile(Boolean isComplexeFile) {
		this.isComplexeFile = isComplexeFile;
	}

	public Boolean getIsDocumentScanning() {
		return isDocumentScanning;
	}

	public void setIsDocumentScanning(Boolean isDocumentScanning) {
		this.isDocumentScanning = isDocumentScanning;
	}

	public Boolean getIsEmergency() {
		return isEmergency;
	}

	public void setIsEmergency(Boolean isEmergency) {
		this.isEmergency = isEmergency;
	}

	public Boolean getIsVacationUpdateBeneficialOwners() {
		return isVacationUpdateBeneficialOwners;
	}

	public void setIsVacationUpdateBeneficialOwners(Boolean isVacationUpdateBeneficialOwners) {
		this.isVacationUpdateBeneficialOwners = isVacationUpdateBeneficialOwners;
	}

	public Boolean getIsFormalityAdditionalDeclaration() {
		return isFormalityAdditionalDeclaration;
	}

	public void setIsFormalityAdditionalDeclaration(Boolean isFormalityAdditionalDeclaration) {
		this.isFormalityAdditionalDeclaration = isFormalityAdditionalDeclaration;
	}

	public Boolean getIsCorrespondenceFees() {
		return isCorrespondenceFees;
	}

	public void setIsCorrespondenceFees(Boolean isCorrespondenceFees) {
		this.isCorrespondenceFees = isCorrespondenceFees;
	}

	public Boolean getIsPublicationPaper() {
		return isPublicationPaper;
	}

	public void setIsPublicationPaper(Boolean isPublicationPaper) {
		this.isPublicationPaper = isPublicationPaper;
	}

	public Integer getPublicationPaperAffaireNumber() {
		return publicationPaperAffaireNumber;
	}

	public void setPublicationPaperAffaireNumber(Integer publicationPaperAffaireNumber) {
		this.publicationPaperAffaireNumber = publicationPaperAffaireNumber;
	}

	public Integer getPublicationPaperClientNumber() {
		return publicationPaperClientNumber;
	}

	public void setPublicationPaperClientNumber(Integer publicationPaperClientNumber) {
		this.publicationPaperClientNumber = publicationPaperClientNumber;
	}

	public SimpleProvision getSimpleProvision() {
		return simpleProvision;
	}

	public void setSimpleProvision(SimpleProvision simpleProvision) {
		this.simpleProvision = simpleProvision;
	}

	public Boolean getIsBilan() {
		return isBilan;
	}

	public void setIsBilan(Boolean isBilan) {
		this.isBilan = isBilan;
	}

	public Boolean getIsApplicationFees() {
		return isApplicationFees;
	}

	public void setIsApplicationFees(Boolean isApplicationFees) {
		this.isApplicationFees = isApplicationFees;
	}

	public Boolean getIsBaloPublicationFlag() {
		return isBaloPublicationFlag;
	}

	public void setIsBaloPublicationFlag(Boolean isBaloPublicationFlag) {
		this.isBaloPublicationFlag = isBaloPublicationFlag;
	}

	public Boolean getIsBaloNormalization() {
		return isBaloNormalization;
	}

	public void setIsBaloNormalization(Boolean isBaloNormalization) {
		this.isBaloNormalization = isBaloNormalization;
	}

	public List<Invoice> getProviderInvoices() {
		return providerInvoices;
	}

	public void setProviderInvoices(List<Invoice> providerInvoices) {
		this.providerInvoices = providerInvoices;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	public Boolean getIsRneUpdate() {
		return isRneUpdate;
	}

	public void setIsRneUpdate(Boolean isRneUpdate) {
		this.isRneUpdate = isRneUpdate;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

}
