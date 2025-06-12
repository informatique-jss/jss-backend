package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.quotation.model.Confrere;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.tiers.model.BillingClosureRecipientType;
import com.jss.osiris.modules.osiris.tiers.model.BillingClosureType;
import com.jss.osiris.modules.osiris.tiers.model.BillingLabelType;
import com.jss.osiris.modules.osiris.tiers.model.PaymentDeadlineType;
import com.jss.osiris.modules.osiris.tiers.model.RefundType;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_tiers_document", columnList = "id_tiers"),
		@Index(name = "idx_responsable_document", columnList = "id_responsable"),
		@Index(name = "idx_confrere_document", columnList = "id_confrere"),
		@Index(name = "idx_customer_order_document", columnList = "id_customer_order"),
		@Index(name = "idx_quotation_document", columnList = "id_quotation"),
})
public class Document implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "document_sequence", sequenceName = "document_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_sequence")
	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "id_tiers")
	@JsonIgnoreProperties(value = { "documents" }, allowSetters = true)
	private Tiers tiers;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "id_confrere")
	@JsonIgnoreProperties(value = { "documents" }, allowSetters = true)
	private Confrere confrere;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "id_responsable")
	@JsonIgnoreProperties(value = { "documents" }, allowSetters = true)
	private Responsable responsable;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "id_quotation")
	@JsonIgnoreProperties(value = { "documents" }, allowSetters = true)
	private Quotation quotation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "id_customer_order")
	@JsonIgnoreProperties(value = { "documents" }, allowSetters = true)
	private CustomerOrder customerOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_document_type")
	@JsonView({ JacksonViews.MyJssDetailedView.class })
	private DocumentType documentType;

	@Column(nullable = false)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Boolean isRecipientClient;

	@Column(nullable = false)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Boolean isRecipientAffaire;

	@Column(length = 200)
	private String affaireAddress;
	@Column(length = 200)
	private String affaireRecipient;
	@Column(length = 200)
	private String clientAddress;
	@Column(length = 200)
	private String clientRecipient;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_billing_label_type")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private BillingLabelType billingLabelType;

	@ManyToMany
	@JoinTable(name = "asso_document_mail_client", joinColumns = @JoinColumn(name = "id_tiers_document"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private List<Mail> mailsClient;

	@ManyToMany
	@JoinTable(name = "asso_document_mail_affaire", joinColumns = @JoinColumn(name = "id_tiers_document"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private List<Mail> mailsAffaire;

	private Boolean isResponsableOnBilling;

	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Boolean isCommandNumberMandatory;

	@Column(length = 40)
	@IndexedField
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String commandNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_payment_deadline_type")
	private PaymentDeadlineType paymentDeadlineType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_refund_type")
	private RefundType refundType;

	@Column(length = 40)
	private String refundIBAN;

	@Column(length = 40)
	private String refundBic;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_billing_closure_type")
	private BillingClosureType billingClosureType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_billing_closure_recipient_type")
	private BillingClosureRecipientType billingClosureRecipientType;

	@Column(length = 200)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String billingLabel;

	@Column(length = 200)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String billingAddress;

	@Column(length = 60)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String billingPostalCode;

	@Column(length = 20)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String cedexComplement;

	@IndexedField
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String externalReference;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_billing_label_city")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private City billingLabelCity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_billing_label_country")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Country billingLabelCountry;

	private Boolean billingLabelIsIndividual;

	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Boolean addToClientMailList;

	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Boolean addToAffaireMailList;

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

	public Confrere getConfrere() {
		return confrere;
	}

	public void setConfrere(Confrere confrere) {
		this.confrere = confrere;
	}

	public Responsable getResponsable() {
		return responsable;
	}

	public void setResponsable(Responsable responsable) {
		this.responsable = responsable;
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

	public String getAffaireRecipient() {
		return affaireRecipient;
	}

	public void setAffaireRecipient(String affaireRecipient) {
		this.affaireRecipient = affaireRecipient;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientRecipient() {
		return clientRecipient;
	}

	public void setClientRecipient(String clientRecipient) {
		this.clientRecipient = clientRecipient;
	}

	public BillingLabelType getBillingLabelType() {
		return billingLabelType;
	}

	public void setBillingLabelType(BillingLabelType billingLabelType) {
		this.billingLabelType = billingLabelType;
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

	public String getBillingLabel() {
		return billingLabel;
	}

	public void setBillingLabel(String billingLabel) {
		this.billingLabel = billingLabel;
	}

	public String getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(String billingAddress) {
		this.billingAddress = billingAddress;
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

	public String getCedexComplement() {
		return cedexComplement;
	}

	public void setCedexComplement(String cedexComplement) {
		this.cedexComplement = cedexComplement;
	}

	public String getRefundBic() {
		return refundBic;
	}

	public void setRefundBic(String refundBic) {
		this.refundBic = refundBic;
	}

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public Boolean getAddToClientMailList() {
		return addToClientMailList;
	}

	public void setAddToClientMailList(Boolean addToClientMailList) {
		this.addToClientMailList = addToClientMailList;
	}

	public Boolean getAddToAffaireMailList() {
		return addToAffaireMailList;
	}

	public void setAddToAffaireMailList(Boolean addToAffaireMailList) {
		this.addToAffaireMailList = addToAffaireMailList;
	}

}
