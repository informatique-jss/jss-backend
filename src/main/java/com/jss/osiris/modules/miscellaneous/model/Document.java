package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.tiers.model.BillingClosureRecipientType;
import com.jss.osiris.modules.tiers.model.BillingClosureType;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.PaymentDeadlineType;
import com.jss.osiris.modules.tiers.model.RefundType;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Entity
@Table(indexes = { @Index(name = "idx_tiers_document", columnList = "id_tiers"),
		@Index(name = "idx_responsable_document", columnList = "id_responsable") })
public class Document implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_sequence")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_tiers")
	@JsonIgnoreProperties(value = { "documents" }, allowSetters = true)
	private Tiers tiers;

	@ManyToOne
	@JoinColumn(name = "id_confrere")
	@JsonIgnoreProperties(value = { "documents" }, allowSetters = true)
	private Confrere confrere;

	@ManyToOne
	@JoinColumn(name = "id_responsable")
	@JsonIgnoreProperties(value = { "documents" }, allowSetters = true)
	private Responsable responsable;

	@ManyToOne
	@JoinColumn(name = "id_quotation")
	@JsonIgnoreProperties(value = { "documents" }, allowSetters = true)
	private Quotation quotation;

	@ManyToOne
	@JoinColumn(name = "id_customer_order")
	@JsonIgnoreProperties(value = { "documents" }, allowSetters = true)
	private CustomerOrder customerOrder;

	@ManyToOne
	@JoinColumn(name = "id_document_type")
	private DocumentType documentType;

	@Column(nullable = false)
	private Boolean isRecipientClient;
	@Column(nullable = false)
	private Boolean isRecipientAffaire;
	@Column(length = 60)
	private String affaireAddress;
	@Column(length = 40)
	private String affaireRecipient;
	@Column(length = 60)
	private String clientAddress;
	@Column(length = 40)
	private String clientRecipient;
	@Column(nullable = false)
	private Boolean isMailingPaper;
	@Column(nullable = false)
	private Boolean isMailingPdf;
	private Integer numberMailingAffaire;
	private Integer numberMailingClient;

	@ManyToOne
	@JoinColumn(name = "id_billing_label_type")
	private BillingLabelType billingLabelType;

	@ManyToMany
	@JoinTable(name = "asso_document_mail_client", joinColumns = @JoinColumn(name = "id_tiers_document"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mailsClient;

	@ManyToMany
	@JoinTable(name = "asso_document_mail_cc_responsable_client", joinColumns = @JoinColumn(name = "id_tiers_document"), inverseJoinColumns = @JoinColumn(name = "id_responsable"))
	private List<Responsable> mailsCCResponsableClient;

	@ManyToMany
	@JoinTable(name = "asso_document_mail_affaire", joinColumns = @JoinColumn(name = "id_tiers_document"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mailsAffaire;

	@ManyToMany
	@JoinTable(name = "asso_document_mail_cc_responsable_affaire", joinColumns = @JoinColumn(name = "id_tiers_document"), inverseJoinColumns = @JoinColumn(name = "id_responsable"))
	private List<Responsable> mailsCCResponsableAffaire;

	private Boolean isResponsableOnBilling;
	private Boolean isCommandNumberMandatory;

	@Column(length = 40)
	private String commandNumber;

	@ManyToOne
	@JoinColumn(name = "id_payment_deadline_type")
	private PaymentDeadlineType paymentDeadlineType;

	@ManyToOne
	@JoinColumn(name = "id_refund_type")
	private RefundType refundType;

	@Column(length = 40)
	private String refundIBAN;

	private Boolean isRefundable;

	@ManyToOne
	@JoinColumn(name = "id_billing_closure_type")
	private BillingClosureType billingClosureType;

	@ManyToOne
	@JoinColumn(name = "id_billing_closure_recipient_type")
	private BillingClosureRecipientType billingClosureRecipientType;

	@Column(length = 60)
	private String billingLabel;

	@Column(length = 60)
	private String billingAddress;

	@Column(length = 60)
	private String billingPostalCode;

	@ManyToOne
	@JoinColumn(name = "id_billing_label_city")
	private City billingLabelCity;

	@ManyToOne
	@JoinColumn(name = "id_billing_label_country")
	private Country billingLabelCountry;

	private Boolean billingLabelIsIndividual;

	@ManyToOne
	@JoinColumn(name = "id_regie")
	private Regie regie;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Tiers getTiers() {
		return tiers;
	}

	public Responsable getResponsable() {
		return responsable;
	}

	public void setResponsable(Responsable responsable) {
		this.responsable = responsable;
	}

	public List<Responsable> getMailsCCResponsableClient() {
		return mailsCCResponsableClient;
	}

	public void setMailsCCResponsableClient(List<Responsable> mailsCCResponsableClient) {
		this.mailsCCResponsableClient = mailsCCResponsableClient;
	}

	public List<Responsable> getMailsCCResponsableAffaire() {
		return mailsCCResponsableAffaire;
	}

	public void setMailsCCResponsableAffaire(List<Responsable> mailsCCResponsableAffaire) {
		this.mailsCCResponsableAffaire = mailsCCResponsableAffaire;
	}

	public void setTiers(Tiers tiers) {
		this.tiers = tiers;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public Boolean getIsRecipientClient() {
		return isRecipientClient;
	}

	public void setIsRecipientClient(Boolean isRecipientClient) {
		this.isRecipientClient = isRecipientClient;
	}

	public Boolean getIsRecipientAffaire() {
		return isRecipientAffaire;
	}

	public void setIsRecipientAffaire(Boolean isRecipientAffaire) {
		this.isRecipientAffaire = isRecipientAffaire;
	}

	public String getAffaireAddress() {
		return affaireAddress;
	}

	public void setAffaireAddress(String affaireAddress) {
		this.affaireAddress = affaireAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getAffaireRecipient() {
		return affaireRecipient;
	}

	public void setAffaireRecipient(String affaireRecipient) {
		this.affaireRecipient = affaireRecipient;
	}

	public String getClientRecipient() {
		return clientRecipient;
	}

	public void setClientRecipient(String clientRecipient) {
		this.clientRecipient = clientRecipient;
	}

	public Boolean getIsMailingPaper() {
		return isMailingPaper;
	}

	public void setIsMailingPaper(Boolean isMailingPaper) {
		this.isMailingPaper = isMailingPaper;
	}

	public Boolean getIsMailingPdf() {
		return isMailingPdf;
	}

	public void setIsMailingPdf(Boolean isMailingPdf) {
		this.isMailingPdf = isMailingPdf;
	}

	public Integer getNumberMailingAffaire() {
		return numberMailingAffaire;
	}

	public void setNumberMailingAffaire(Integer numberMailingAffaire) {
		this.numberMailingAffaire = numberMailingAffaire;
	}

	public Integer getNumberMailingClient() {
		return numberMailingClient;
	}

	public void setNumberMailingClient(Integer numberMailingClient) {
		this.numberMailingClient = numberMailingClient;
	}

	public List<Mail> getMailsClient() {
		return mailsClient;
	}

	public void setMailsClient(List<Mail> mailsClient) {
		this.mailsClient = mailsClient;
	}

	public List<Mail> getMailsAffaire() {
		return mailsAffaire;
	}

	public void setMailsAffaire(List<Mail> mailsAffaire) {
		this.mailsAffaire = mailsAffaire;
	}

	public BillingLabelType getBillingLabelType() {
		return billingLabelType;
	}

	public void setBillingLabelType(BillingLabelType billingLabelType) {
		this.billingLabelType = billingLabelType;
	}

	public Boolean getIsResponsableOnBilling() {
		return isResponsableOnBilling;
	}

	public void setIsResponsableOnBilling(Boolean isResponsableOnBilling) {
		this.isResponsableOnBilling = isResponsableOnBilling;
	}

	public Boolean getIsCommandNumberMandatory() {
		return isCommandNumberMandatory;
	}

	public void setIsCommandNumberMandatory(Boolean isCommandNumberMandatory) {
		this.isCommandNumberMandatory = isCommandNumberMandatory;
	}

	public String getCommandNumber() {
		return commandNumber;
	}

	public void setCommandNumber(String commandNumber) {
		this.commandNumber = commandNumber;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public PaymentDeadlineType getPaymentDeadlineType() {
		return paymentDeadlineType;
	}

	public void setPaymentDeadlineType(PaymentDeadlineType paymentDeadlineType) {
		this.paymentDeadlineType = paymentDeadlineType;
	}

	public RefundType getRefundType() {
		return refundType;
	}

	public void setRefundType(RefundType refundType) {
		this.refundType = refundType;
	}

	public String getRefundIBAN() {
		return refundIBAN;
	}

	public void setRefundIBAN(String refundIBAN) {
		this.refundIBAN = refundIBAN;
	}

	public Boolean getIsRefundable() {
		return isRefundable;
	}

	public void setIsRefundable(Boolean isRefundable) {
		this.isRefundable = isRefundable;
	}

	public BillingClosureType getBillingClosureType() {
		return billingClosureType;
	}

	public void setBillingClosureType(BillingClosureType billingClosureType) {
		this.billingClosureType = billingClosureType;
	}

	public BillingClosureRecipientType getBillingClosureRecipientType() {
		return billingClosureRecipientType;
	}

	public void setBillingClosureRecipientType(BillingClosureRecipientType billingClosureRecipientType) {
		this.billingClosureRecipientType = billingClosureRecipientType;
	}

	public Quotation getQuotation() {
		return quotation;
	}

	public void setQuotation(Quotation quotation) {
		this.quotation = quotation;
	}

	public Confrere getConfrere() {
		return confrere;
	}

	public void setConfrere(Confrere confrere) {
		this.confrere = confrere;
	}

	public String getBillingLabel() {
		return billingLabel;
	}

	public void setBillingLabel(String billingLabel) {
		this.billingLabel = billingLabel;
	}

	public String getBillingPostalCode() {
		return billingPostalCode;
	}

	public void setBillingPostalCode(String billingPostalCode) {
		this.billingPostalCode = billingPostalCode;
	}

	public City getBillingLabelCity() {
		return billingLabelCity;
	}

	public void setBillingLabelCity(City billingLabelCity) {
		this.billingLabelCity = billingLabelCity;
	}

	public Country getBillingLabelCountry() {
		return billingLabelCountry;
	}

	public void setBillingLabelCountry(Country billingLabelCountry) {
		this.billingLabelCountry = billingLabelCountry;
	}

	public Boolean getBillingLabelIsIndividual() {
		return billingLabelIsIndividual;
	}

	public void setBillingLabelIsIndividual(Boolean billingLabelIsIndividual) {
		this.billingLabelIsIndividual = billingLabelIsIndividual;
	}

	public String getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(String billingAddress) {
		this.billingAddress = billingAddress;
	}

	public Regie getRegie() {
		return regie;
	}

	public void setRegie(Regie regie) {
		this.regie = regie;
	}

}
