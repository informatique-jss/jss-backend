package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeDeserializer;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.mail.model.CustomerMail;
import com.jss.osiris.modules.osiris.invoicing.model.AzureInvoice;
import com.jss.osiris.modules.osiris.invoicing.model.AzureReceipt;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.MissingAttachmentQuery;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.PiecesJointe;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDocument;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.DocumentAssocieInfogreffe;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_tiers_attachment", columnList = "id_tiers"),
		@Index(name = "idx_customer_order_attachment", columnList = "id_customer_order"),
		@Index(name = "idx_quotation_attachment", columnList = "id_quotation"),
		@Index(name = "idx_customer_mail_attachment", columnList = "id_customer_mail"),
		@Index(name = "idx_missing_attachment_query_attachment", columnList = "id_missing_attachment_query"),
		@Index(name = "idx_provider_attachment", columnList = "id_provider"),
		@Index(name = "idx_competent_authority_attachment", columnList = "id_competent_authority"),
		@Index(name = "idx_providion_attachment", columnList = "id_provision"),
		@Index(name = "idx_invoice_attachment", columnList = "id_invoice"),
		@Index(name = "idx_affaire_attachment", columnList = "id_affaire"),
		@Index(name = "idx_type_document_attachment", columnList = "id_type_document_attachment"),
		@Index(name = "idx_parent_attachment", columnList = "id_parent_attachment"),
		@Index(name = "idx_piece_jointe_attachment", columnList = "id_piece_jointe"),
		@Index(name = "idx_azure_invoice_attachment", columnList = "id_azure_invoice"),
		@Index(name = "idx_azure_receipt_attachment", columnList = "id_azure_receipt"),
		@Index(name = "idx_uploaded_file_attachment", columnList = "id_uploaded_file"),
		@Index(name = "idx_responsable_attachment", columnList = "id_responsable"),
		@Index(name = "idx_asso_service_document_attachment", columnList = "id_asso_service_document"),
		@Index(name = "idx_document_associe_infogreffe", columnList = "id_document_associe_infogreffe") })
public class Attachment implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "attachment_sequence", sequenceName = "attachment_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_sequence")
	@JsonView(JacksonViews.MyJssView.class)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "id_tiers")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private Tiers tiers;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "id_customer_mail")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private CustomerMail customerMail;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_missing_attachment_query")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private MissingAttachmentQuery missingAttachmentQuery;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "id_responsable")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private Responsable responsable;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "id_provider")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private Provider provider;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "id_competent_authority")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private CompetentAuthority competentAuthority;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "id_quotation")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private Quotation quotation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provision")
	@JsonIgnoreProperties(value = { "attachments", "providerInvoices" }, allowSetters = true)
	@JsonIgnore
	private Provision provision;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "id_customer_order")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private CustomerOrder customerOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_invoice")
	@JsonIgnore
	@JsonIgnoreProperties(value = { "attachments", "provider", "customerOrder", "accountingRecords",
			"customerOrderForInboundInvoice", "competentAuthority", "invoiceItems",
			"azureInvoice" }, allowSetters = true)
	private Invoice invoice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_affaire")
	@JsonIgnore
	@JsonIgnoreProperties(value = { "attachments", "provider", "customerOrder", "accountingRecords",
			"customerOrderForInboundInvoice", "competentAuthority", "invoiceItems",
			"azureInvoice" }, allowSetters = true)
	private Affaire affaire;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_asso_service_document")
	@JsonIgnore
	@JsonIgnoreProperties(value = { "attachments", "service" }, allowSetters = true)
	private AssoServiceDocument assoServiceDocument;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_attachment_type")
	@JsonView(JacksonViews.MyJssView.class)
	private AttachmentType attachmentType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_type_document")
	@JsonView(JacksonViews.MyJssView.class)
	private TypeDocument typeDocument;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_type_document_attachment")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private TypeDocument typeDocumentAttachment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_uploaded_file")
	private UploadedFile uploadedFile;

	@Column(nullable = false)
	private Boolean isDisabled;

	@Column(length = 2000)
	@JsonView(JacksonViews.MyJssView.class)
	private String description;

	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	@JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
	@JsonView(JacksonViews.MyJssView.class)
	private LocalDateTime creatDateTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_azure_invoice")
	@JsonIgnoreProperties(value = { "attachments", "invoices" }, allowSetters = true)
	private AzureInvoice azureInvoice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_azure_receipt")
	@JsonIgnoreProperties(value = { "attachments", "invoices" }, allowSetters = true)
	private AzureReceipt azureReceipt;

	private Boolean isAlreadySent;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JsonIgnore
	@JoinColumn(name = "id_parent_attachment")
	private Attachment parentAttachment;

	@OneToMany(mappedBy = "parentAttachment")
	@JsonIgnore
	private List<Attachment> childrenAttachments;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_piece_jointe")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private PiecesJointe piecesJointe;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_document_associe_infogreffe")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private DocumentAssocieInfogreffe documentAssocieInfogreffe;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Tiers getTiers() {
		return tiers;
	}

	public void setTiers(Tiers tiers) {
		this.tiers = tiers;
	}

	public Responsable getResponsable() {
		return responsable;
	}

	public void setResponsable(Responsable responsable) {
		this.responsable = responsable;
	}

	public AttachmentType getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(AttachmentType attachmentType) {
		this.attachmentType = attachmentType;
	}

	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public Quotation getQuotation() {
		return quotation;
	}

	public void setQuotation(Quotation quotation) {
		this.quotation = quotation;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public Provision getProvision() {
		return provision;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}

	public CustomerMail getCustomerMail() {
		return customerMail;
	}

	public void setCustomerMail(CustomerMail customerMail) {
		this.customerMail = customerMail;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getCreatDateTime() {
		return creatDateTime;
	}

	public void setCreatDateTime(LocalDateTime creatDateTime) {
		this.creatDateTime = creatDateTime;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public CompetentAuthority getCompetentAuthority() {
		return competentAuthority;
	}

	public void setCompetentAuthority(CompetentAuthority competentAuthority) {
		this.competentAuthority = competentAuthority;
	}

	public AzureInvoice getAzureInvoice() {
		return azureInvoice;
	}

	public void setAzureInvoice(AzureInvoice azureInvoice) {
		this.azureInvoice = azureInvoice;
	}

	public AzureReceipt getAzureReceipt() {
		return azureReceipt;
	}

	public void setAzureReceipt(AzureReceipt azureReceipt) {
		this.azureReceipt = azureReceipt;
	}

	public Boolean getIsAlreadySent() {
		return isAlreadySent;
	}

	public void setIsAlreadySent(Boolean isAlreadySent) {
		this.isAlreadySent = isAlreadySent;
	}

	public Attachment getParentAttachment() {
		return parentAttachment;
	}

	public List<Attachment> getChildrenAttachments() {
		return childrenAttachments;
	}

	public void setChildrenAttachments(List<Attachment> childrenAttachments) {
		this.childrenAttachments = childrenAttachments;
	}

	public void setParentAttachment(Attachment parentAttachment) {
		this.parentAttachment = parentAttachment;
	}

	public PiecesJointe getPiecesJointe() {
		return piecesJointe;
	}

	public void setPiecesJointe(PiecesJointe piecesJointe) {
		this.piecesJointe = piecesJointe;
	}

	public Affaire getAffaire() {
		return affaire;
	}

	public void setAffaire(Affaire affaire) {
		this.affaire = affaire;
	}

	public TypeDocument getTypeDocument() {
		return typeDocument;
	}

	public void setTypeDocument(TypeDocument typeDocument) {
		this.typeDocument = typeDocument;
	}

	public AssoServiceDocument getAssoServiceDocument() {
		return assoServiceDocument;
	}

	public void setAssoServiceDocument(AssoServiceDocument assoServiceDocument) {
		this.assoServiceDocument = assoServiceDocument;
	}

	public TypeDocument getTypeDocumentAttachment() {
		return typeDocumentAttachment;
	}

	public void setTypeDocumentAttachment(TypeDocument typeDocumentAttachment) {
		this.typeDocumentAttachment = typeDocumentAttachment;
	}

	public DocumentAssocieInfogreffe getDocumentAssocieInfogreffe() {
		return documentAssocieInfogreffe;
	}

	public void setDocumentAssocieInfogreffe(DocumentAssocieInfogreffe documentAssocieInfogreffe) {
		this.documentAssocieInfogreffe = documentAssocieInfogreffe;
	}

	public MissingAttachmentQuery getMissingAttachmentQuery() {
		return missingAttachmentQuery;
	}

	public void setMissingAttachmentQuery(MissingAttachmentQuery missingAttachmentQuery) {
		this.missingAttachmentQuery = missingAttachmentQuery;
	}

}
