package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.invoicing.model.PaymentWay;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.ActType;
import com.jss.osiris.modules.quotation.model.AssignationType;
import com.jss.osiris.modules.quotation.model.BodaccPublicationType;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.DomiciliationContractType;
import com.jss.osiris.modules.quotation.model.JournalType;
import com.jss.osiris.modules.quotation.model.MailRedirectionType;
import com.jss.osiris.modules.quotation.model.QuotationLabelType;
import com.jss.osiris.modules.quotation.model.TransfertFundsType;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeJuridique;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeFormalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypePersonne;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.RefundType;
import com.jss.osiris.modules.tiers.model.SubscriptionPeriodType;
import com.jss.osiris.modules.tiers.model.TiersType;

@Entity
public class Constant implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_billing_label_type_code_affaire")
	private BillingLabelType billingLabelTypeCodeAffaire;

	@ManyToOne
	@JoinColumn(name = "id_billing_label_type_other")
	private BillingLabelType billingLabelTypeOther;

	@ManyToOne
	@JoinColumn(name = "id_billing_label_type_customer")
	private BillingLabelType billingLabelTypeCustomer;

	@ManyToOne
	@JoinColumn(name = "id_accounting_journal_sales")
	private AccountingJournal accountingJournalSales;

	@ManyToOne
	@JoinColumn(name = "id_accounting_journal_purchases")
	private AccountingJournal accountingJournalPurchases;

	@ManyToOne
	@JoinColumn(name = "id_accounting_journal_anouveau")
	private AccountingJournal accountingJournalANouveau;

	@ManyToOne
	@JoinColumn(name = "id_tiers_type_prospect")
	private TiersType tiersTypeProspect;

	@ManyToOne
	@JoinColumn(name = "id_document_type_publication")
	private DocumentType documentTypePublication;

	@ManyToOne
	@JoinColumn(name = "id_document_type_cfe")
	private DocumentType documentTypeCfe;

	@ManyToOne
	@JoinColumn(name = "id_document_type_kbis")
	private DocumentType documentTypeKbis;

	@ManyToOne
	@JoinColumn(name = "id_document_type_billing")
	private DocumentType documentTypeBilling;

	@ManyToOne
	@JoinColumn(name = "id_document_type_dunning")
	private DocumentType documentTypeDunning;

	@ManyToOne
	@JoinColumn(name = "id_document_type_refund")
	private DocumentType documentTypeRefund;

	@ManyToOne
	@JoinColumn(name = "id_document_type_billing_closure")
	private DocumentType documentTypeBillingClosure;

	@ManyToOne
	@JoinColumn(name = "id_document_type_provisionnal_receipt")
	private DocumentType documentTypeProvisionnalReceipt;

	@ManyToOne
	@JoinColumn(name = "id_document_type_proof_reading")
	private DocumentType documentTypeProofReading;

	@ManyToOne
	@JoinColumn(name = "id_document_type_publication_certificate")
	private DocumentType documentTypePublicationCertificate;

	@ManyToOne
	@JoinColumn(name = "id_document_type_quotation")
	private DocumentType documentTypeQuotation;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_kbis")
	private AttachmentType attachmentTypeKbis;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_cni")
	private AttachmentType attachmentTypeCni;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_logo")
	private AttachmentType attachmentTypeLogo;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_proof_of_address")
	private AttachmentType attachmentTypeProofOfAddress;

	@ManyToOne
	@JoinColumn(name = "id_country_france")
	private Country countryFrance;

	@ManyToOne
	@JoinColumn(name = "id_country_monaco")
	private Country countryMonaco;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_logo")
	private BillingType billingTypeLogo;

	@ManyToOne
	@JoinColumn(name = "id_quotation_label_type_other")
	private QuotationLabelType quotationLabelTypeOther;

	@ManyToOne
	@JoinColumn(name = "id_payment_type_prelevement")
	private PaymentType paymentTypePrelevement;

	@ManyToOne
	@JoinColumn(name = "id_payment_type_virement")
	private PaymentType paymentTypeVirement;

	@ManyToOne
	@JoinColumn(name = "id_payment_type_cb")
	private PaymentType paymentTypeCB;

	@ManyToOne
	@JoinColumn(name = "id_payment_type_especes")
	private PaymentType paymentTypeEspeces;

	@ManyToOne
	@JoinColumn(name = "id_refund_type_virement")
	private RefundType refundTypeVirement;

	@ManyToOne
	@JoinColumn(name = "id_subscription_period_type_12m")
	private SubscriptionPeriodType subscriptionPeriodType12M;

	@ManyToOne
	@JoinColumn(name = "id_legal_form_unregistered")
	private LegalForm legalFormUnregistered;

	@ManyToOne
	@JoinColumn(name = "id_journal_type_spel")
	private JournalType journalTypeSpel;

	@ManyToOne
	@JoinColumn(name = "id_confrere_jss")
	private Confrere confrereJss;

	@ManyToOne
	@JoinColumn(name = "id_domiciliation_constract_type_keep_mail")
	private DomiciliationContractType domiciliationContractTypeKeepMail;

	@ManyToOne
	@JoinColumn(name = "id_domiciliation_constract_type_route_mail")
	private DomiciliationContractType domiciliationContractTypeRouteMail;

	@ManyToOne
	@JoinColumn(name = "id_domiciliation_constract_type_route_email_mail")
	private DomiciliationContractType domiciliationContractTypeRouteEmailAndMail;

	@ManyToOne
	@JoinColumn(name = "id_domiciliation_constract_type_route_email")
	private DomiciliationContractType domiciliationContractTypeRouteEmail;

	@ManyToOne
	@JoinColumn(name = "id_mail_redirection_type_other")
	private MailRedirectionType mailRedirectionTypeOther;

	@ManyToOne
	@JoinColumn(name = "id_bodacc_publication_type_merging")
	private BodaccPublicationType bodaccPublicationTypeMerging;

	@ManyToOne
	@JoinColumn(name = "id_bodacc_publication_type_split")
	private BodaccPublicationType bodaccPublicationTypeSplit;

	@ManyToOne
	@JoinColumn(name = "id_bodacc_publication_type_partial_split")
	private BodaccPublicationType bodaccPublicationTypePartialSplit;

	@ManyToOne
	@JoinColumn(name = "id_bodacc_publication_type_possession_dispatch")
	private BodaccPublicationType bodaccPublicationTypePossessionDispatch;

	@ManyToOne
	@JoinColumn(name = "id_bodacc_publication_type_estate_representative_designation")
	private BodaccPublicationType bodaccPublicationTypeEstateRepresentativeDesignation;

	@ManyToOne
	@JoinColumn(name = "id_bodacc_publication_type_sale_of_business")
	private BodaccPublicationType bodaccPublicationTypeSaleOfBusiness;

	@ManyToOne
	@JoinColumn(name = "id_act_type_seing")
	private ActType actTypeSeing;

	@ManyToOne
	@JoinColumn(name = "id_act_type_authentic")
	private ActType actTypeAuthentic;

	@ManyToOne
	@JoinColumn(name = "id_assignation_type_employee")
	private AssignationType assignationTypeEmployee;

	@ManyToOne
	@JoinColumn(name = "id_employee_billing_responsible")
	private Employee employeeBillingResponsible;

	@ManyToOne
	@JoinColumn(name = "id_transfert_funds_type_physique")
	private TransfertFundsType transfertFundsTypePhysique;

	@ManyToOne
	@JoinColumn(name = "id_transfert_funds_type_moral")
	private TransfertFundsType transfertFundsTypeMoral;

	@ManyToOne
	@JoinColumn(name = "id_transfert_funds_type_bail")
	private TransfertFundsType transfertFundsTypeBail;

	@ManyToOne
	@JoinColumn(name = "id_competent_authority_type_rcs")
	private CompetentAuthorityType competentAuthorityTypeRcs;

	@ManyToOne
	@JoinColumn(name = "id_competent_authority_type_cfp")
	private CompetentAuthorityType competentAuthorityTypeCfp;

	@ManyToOne
	@JoinColumn(name = "id_invoice_status_send")
	private InvoiceStatus invoiceStatusSend;

	@ManyToOne
	@JoinColumn(name = "id_invoice_status_payed")
	private InvoiceStatus invoiceStatusPayed;

	@ManyToOne
	@JoinColumn(name = "id_invoice_status_cancelled")
	private InvoiceStatus invoiceStatusCancelled;

	@ManyToOne
	@JoinColumn(name = "id_payment_way_inbound")
	private PaymentWay paymentWayInbound;

	@ManyToOne
	@JoinColumn(name = "id_payment_way_outbound")
	private PaymentWay paymentWayOutboud;

	@ManyToOne
	@JoinColumn(name = "id_vat_twenty")
	private Vat vatTwenty;

	@ManyToOne
	@JoinColumn(name = "id_vat_eight")
	private Vat vatEight;

	@ManyToOne
	@JoinColumn(name = "id_department_martinique")
	private Department departmentMartinique;

	@ManyToOne
	@JoinColumn(name = "id_department_guadeloupe")
	private Department departmentGuadeloupe;

	@ManyToOne
	@JoinColumn(name = "id_department_reunion")
	private Department departmentReunion;

	@ManyToOne
	@JoinColumn(name = "id_type_personne_personne_physique")
	private TypePersonne typePersonnePersonnePhysique;

	@ManyToOne
	@JoinColumn(name = "id_type_personne_personne_exploitation")
	private TypePersonne typePersonneExploitation;

	@ManyToOne
	@JoinColumn(name = "id_type_personne_personne_morale")
	private TypePersonne typePersonnePersonneMorale;

	@ManyToOne
	@JoinColumn(name = "id_forme_juridique_entrepreneur_individuel")
	private FormeJuridique formeJuridiqueEntrepreneurIndividuel;

	@ManyToOne
	@JoinColumn(name = "id_type_formalite_cessation")
	private TypeFormalite typeFormaliteCessation;

	@ManyToOne
	@JoinColumn(name = "id_type_formalite_correction")
	private TypeFormalite typeFormaliteCorrection;

	@ManyToOne
	@JoinColumn(name = "id_type_formalite_modification")
	private TypeFormalite typeFormaliteModification;

	@ManyToOne
	@JoinColumn(name = "id_type_formalite_creation")
	private TypeFormalite typeFormaliteCreation;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BillingLabelType getBillingLabelTypeCodeAffaire() {
		return billingLabelTypeCodeAffaire;
	}

	public void setBillingLabelTypeCodeAffaire(BillingLabelType billingLabelTypeCodeAffaire) {
		this.billingLabelTypeCodeAffaire = billingLabelTypeCodeAffaire;
	}

	public BillingLabelType getBillingLabelTypeOther() {
		return billingLabelTypeOther;
	}

	public void setBillingLabelTypeOther(BillingLabelType billingLabelTypeOther) {
		this.billingLabelTypeOther = billingLabelTypeOther;
	}

	public BillingLabelType getBillingLabelTypeCustomer() {
		return billingLabelTypeCustomer;
	}

	public void setBillingLabelTypeCustomer(BillingLabelType billingLabelTypeCustomer) {
		this.billingLabelTypeCustomer = billingLabelTypeCustomer;
	}

	public AccountingJournal getAccountingJournalSales() {
		return accountingJournalSales;
	}

	public void setAccountingJournalSales(AccountingJournal accountingJournalSales) {
		this.accountingJournalSales = accountingJournalSales;
	}

	public AccountingJournal getAccountingJournalPurchases() {
		return accountingJournalPurchases;
	}

	public void setAccountingJournalPurchases(AccountingJournal accountingJournalPurchases) {
		this.accountingJournalPurchases = accountingJournalPurchases;
	}

	public AccountingJournal getAccountingJournalANouveau() {
		return accountingJournalANouveau;
	}

	public void setAccountingJournalANouveau(AccountingJournal accountingJournalANouveau) {
		this.accountingJournalANouveau = accountingJournalANouveau;
	}

	public TiersType getTiersTypeProspect() {
		return tiersTypeProspect;
	}

	public void setTiersTypeProspect(TiersType tiersTypeProspect) {
		this.tiersTypeProspect = tiersTypeProspect;
	}

	public DocumentType getDocumentTypePublication() {
		return documentTypePublication;
	}

	public void setDocumentTypePublication(DocumentType documentTypePublication) {
		this.documentTypePublication = documentTypePublication;
	}

	public DocumentType getDocumentTypeCfe() {
		return documentTypeCfe;
	}

	public void setDocumentTypeCfe(DocumentType documentTypeCfe) {
		this.documentTypeCfe = documentTypeCfe;
	}

	public DocumentType getDocumentTypeKbis() {
		return documentTypeKbis;
	}

	public void setDocumentTypeKbis(DocumentType documentTypeKbis) {
		this.documentTypeKbis = documentTypeKbis;
	}

	public DocumentType getDocumentTypeBilling() {
		return documentTypeBilling;
	}

	public void setDocumentTypeBilling(DocumentType documentTypeBilling) {
		this.documentTypeBilling = documentTypeBilling;
	}

	public DocumentType getDocumentTypeDunning() {
		return documentTypeDunning;
	}

	public void setDocumentTypeDunning(DocumentType documentTypeDunning) {
		this.documentTypeDunning = documentTypeDunning;
	}

	public DocumentType getDocumentTypeRefund() {
		return documentTypeRefund;
	}

	public void setDocumentTypeRefund(DocumentType documentTypeRefund) {
		this.documentTypeRefund = documentTypeRefund;
	}

	public DocumentType getDocumentTypeBillingClosure() {
		return documentTypeBillingClosure;
	}

	public void setDocumentTypeBillingClosure(DocumentType documentTypeBillingClosure) {
		this.documentTypeBillingClosure = documentTypeBillingClosure;
	}

	public DocumentType getDocumentTypeProvisionnalReceipt() {
		return documentTypeProvisionnalReceipt;
	}

	public void setDocumentTypeProvisionnalReceipt(DocumentType documentTypeProvisionnalReceipt) {
		this.documentTypeProvisionnalReceipt = documentTypeProvisionnalReceipt;
	}

	public DocumentType getDocumentTypeProofReading() {
		return documentTypeProofReading;
	}

	public void setDocumentTypeProofReading(DocumentType documentTypeProofReading) {
		this.documentTypeProofReading = documentTypeProofReading;
	}

	public DocumentType getDocumentTypePublicationCertificate() {
		return documentTypePublicationCertificate;
	}

	public void setDocumentTypePublicationCertificate(DocumentType documentTypePublicationCertificate) {
		this.documentTypePublicationCertificate = documentTypePublicationCertificate;
	}

	public DocumentType getDocumentTypeQuotation() {
		return documentTypeQuotation;
	}

	public void setDocumentTypeQuotation(DocumentType documentTypeQuotation) {
		this.documentTypeQuotation = documentTypeQuotation;
	}

	public AttachmentType getAttachmentTypeKbis() {
		return attachmentTypeKbis;
	}

	public void setAttachmentTypeKbis(AttachmentType attachmentTypeKbis) {
		this.attachmentTypeKbis = attachmentTypeKbis;
	}

	public AttachmentType getAttachmentTypeCni() {
		return attachmentTypeCni;
	}

	public void setAttachmentTypeCni(AttachmentType attachmentTypeCni) {
		this.attachmentTypeCni = attachmentTypeCni;
	}

	public AttachmentType getAttachmentTypeLogo() {
		return attachmentTypeLogo;
	}

	public void setAttachmentTypeLogo(AttachmentType attachmentTypeLogo) {
		this.attachmentTypeLogo = attachmentTypeLogo;
	}

	public AttachmentType getAttachmentTypeProofOfAddress() {
		return attachmentTypeProofOfAddress;
	}

	public void setAttachmentTypeProofOfAddress(AttachmentType attachmentTypeProofOfAddress) {
		this.attachmentTypeProofOfAddress = attachmentTypeProofOfAddress;
	}

	public Country getCountryFrance() {
		return countryFrance;
	}

	public void setCountryFrance(Country countryFrance) {
		this.countryFrance = countryFrance;
	}

	public Country getCountryMonaco() {
		return countryMonaco;
	}

	public void setCountryMonaco(Country countryMonaco) {
		this.countryMonaco = countryMonaco;
	}

	public BillingType getBillingTypeLogo() {
		return billingTypeLogo;
	}

	public void setBillingTypeLogo(BillingType billingTypeLogo) {
		this.billingTypeLogo = billingTypeLogo;
	}

	public QuotationLabelType getQuotationLabelTypeOther() {
		return quotationLabelTypeOther;
	}

	public void setQuotationLabelTypeOther(QuotationLabelType quotationLabelTypeOther) {
		this.quotationLabelTypeOther = quotationLabelTypeOther;
	}

	public PaymentType getPaymentTypePrelevement() {
		return paymentTypePrelevement;
	}

	public void setPaymentTypePrelevement(PaymentType paymentTypePrelevement) {
		this.paymentTypePrelevement = paymentTypePrelevement;
	}

	public PaymentType getPaymentTypeVirement() {
		return paymentTypeVirement;
	}

	public void setPaymentTypeVirement(PaymentType paymentTypeVirement) {
		this.paymentTypeVirement = paymentTypeVirement;
	}

	public PaymentType getPaymentTypeCB() {
		return paymentTypeCB;
	}

	public void setPaymentTypeCB(PaymentType paymentTypeCB) {
		this.paymentTypeCB = paymentTypeCB;
	}

	public PaymentType getPaymentTypeEspeces() {
		return paymentTypeEspeces;
	}

	public void setPaymentTypeEspeces(PaymentType paymentTypeEspeces) {
		this.paymentTypeEspeces = paymentTypeEspeces;
	}

	public RefundType getRefundTypeVirement() {
		return refundTypeVirement;
	}

	public void setRefundTypeVirement(RefundType refundTypeVirement) {
		this.refundTypeVirement = refundTypeVirement;
	}

	public SubscriptionPeriodType getSubscriptionPeriodType12M() {
		return subscriptionPeriodType12M;
	}

	public void setSubscriptionPeriodType12M(SubscriptionPeriodType subscriptionPeriodType12M) {
		this.subscriptionPeriodType12M = subscriptionPeriodType12M;
	}

	public LegalForm getLegalFormUnregistered() {
		return legalFormUnregistered;
	}

	public void setLegalFormUnregistered(LegalForm legalFormUnregistered) {
		this.legalFormUnregistered = legalFormUnregistered;
	}

	public JournalType getJournalTypeSpel() {
		return journalTypeSpel;
	}

	public void setJournalTypeSpel(JournalType journalTypeSpel) {
		this.journalTypeSpel = journalTypeSpel;
	}

	public Confrere getConfrereJss() {
		return confrereJss;
	}

	public void setConfrereJss(Confrere confrereJss) {
		this.confrereJss = confrereJss;
	}

	public DomiciliationContractType getDomiciliationContractTypeKeepMail() {
		return domiciliationContractTypeKeepMail;
	}

	public void setDomiciliationContractTypeKeepMail(DomiciliationContractType domiciliationContractTypeKeepMail) {
		this.domiciliationContractTypeKeepMail = domiciliationContractTypeKeepMail;
	}

	public DomiciliationContractType getDomiciliationContractTypeRouteMail() {
		return domiciliationContractTypeRouteMail;
	}

	public void setDomiciliationContractTypeRouteMail(DomiciliationContractType domiciliationContractTypeRouteMail) {
		this.domiciliationContractTypeRouteMail = domiciliationContractTypeRouteMail;
	}

	public DomiciliationContractType getDomiciliationContractTypeRouteEmailAndMail() {
		return domiciliationContractTypeRouteEmailAndMail;
	}

	public void setDomiciliationContractTypeRouteEmailAndMail(
			DomiciliationContractType domiciliationContractTypeRouteEmailAndMail) {
		this.domiciliationContractTypeRouteEmailAndMail = domiciliationContractTypeRouteEmailAndMail;
	}

	public DomiciliationContractType getDomiciliationContractTypeRouteEmail() {
		return domiciliationContractTypeRouteEmail;
	}

	public void setDomiciliationContractTypeRouteEmail(DomiciliationContractType domiciliationContractTypeRouteEmail) {
		this.domiciliationContractTypeRouteEmail = domiciliationContractTypeRouteEmail;
	}

	public MailRedirectionType getMailRedirectionTypeOther() {
		return mailRedirectionTypeOther;
	}

	public void setMailRedirectionTypeOther(MailRedirectionType mailRedirectionTypeOther) {
		this.mailRedirectionTypeOther = mailRedirectionTypeOther;
	}

	public BodaccPublicationType getBodaccPublicationTypeMerging() {
		return bodaccPublicationTypeMerging;
	}

	public void setBodaccPublicationTypeMerging(BodaccPublicationType bodaccPublicationTypeMerging) {
		this.bodaccPublicationTypeMerging = bodaccPublicationTypeMerging;
	}

	public BodaccPublicationType getBodaccPublicationTypeSplit() {
		return bodaccPublicationTypeSplit;
	}

	public void setBodaccPublicationTypeSplit(BodaccPublicationType bodaccPublicationTypeSplit) {
		this.bodaccPublicationTypeSplit = bodaccPublicationTypeSplit;
	}

	public BodaccPublicationType getBodaccPublicationTypePartialSplit() {
		return bodaccPublicationTypePartialSplit;
	}

	public void setBodaccPublicationTypePartialSplit(BodaccPublicationType bodaccPublicationTypePartialSplit) {
		this.bodaccPublicationTypePartialSplit = bodaccPublicationTypePartialSplit;
	}

	public BodaccPublicationType getBodaccPublicationTypePossessionDispatch() {
		return bodaccPublicationTypePossessionDispatch;
	}

	public void setBodaccPublicationTypePossessionDispatch(
			BodaccPublicationType bodaccPublicationTypePossessionDispatch) {
		this.bodaccPublicationTypePossessionDispatch = bodaccPublicationTypePossessionDispatch;
	}

	public BodaccPublicationType getBodaccPublicationTypeEstateRepresentativeDesignation() {
		return bodaccPublicationTypeEstateRepresentativeDesignation;
	}

	public void setBodaccPublicationTypeEstateRepresentativeDesignation(
			BodaccPublicationType bodaccPublicationTypeEstateRepresentativeDesignation) {
		this.bodaccPublicationTypeEstateRepresentativeDesignation = bodaccPublicationTypeEstateRepresentativeDesignation;
	}

	public BodaccPublicationType getBodaccPublicationTypeSaleOfBusiness() {
		return bodaccPublicationTypeSaleOfBusiness;
	}

	public void setBodaccPublicationTypeSaleOfBusiness(BodaccPublicationType bodaccPublicationTypeSaleOfBusiness) {
		this.bodaccPublicationTypeSaleOfBusiness = bodaccPublicationTypeSaleOfBusiness;
	}

	public ActType getActTypeSeing() {
		return actTypeSeing;
	}

	public void setActTypeSeing(ActType actTypeSeing) {
		this.actTypeSeing = actTypeSeing;
	}

	public ActType getActTypeAuthentic() {
		return actTypeAuthentic;
	}

	public void setActTypeAuthentic(ActType actTypeAuthentic) {
		this.actTypeAuthentic = actTypeAuthentic;
	}

	public AssignationType getAssignationTypeEmployee() {
		return assignationTypeEmployee;
	}

	public void setAssignationTypeEmployee(AssignationType assignationTypeEmployee) {
		this.assignationTypeEmployee = assignationTypeEmployee;
	}

	public Employee getEmployeeBillingResponsible() {
		return employeeBillingResponsible;
	}

	public void setEmployeeBillingResponsible(Employee employeeBillingResponsible) {
		this.employeeBillingResponsible = employeeBillingResponsible;
	}

	public TransfertFundsType getTransfertFundsTypePhysique() {
		return transfertFundsTypePhysique;
	}

	public void setTransfertFundsTypePhysique(TransfertFundsType transfertFundsTypePhysique) {
		this.transfertFundsTypePhysique = transfertFundsTypePhysique;
	}

	public TransfertFundsType getTransfertFundsTypeMoral() {
		return transfertFundsTypeMoral;
	}

	public void setTransfertFundsTypeMoral(TransfertFundsType transfertFundsTypeMoral) {
		this.transfertFundsTypeMoral = transfertFundsTypeMoral;
	}

	public TransfertFundsType getTransfertFundsTypeBail() {
		return transfertFundsTypeBail;
	}

	public void setTransfertFundsTypeBail(TransfertFundsType transfertFundsTypeBail) {
		this.transfertFundsTypeBail = transfertFundsTypeBail;
	}

	public CompetentAuthorityType getCompetentAuthorityTypeRcs() {
		return competentAuthorityTypeRcs;
	}

	public void setCompetentAuthorityTypeRcs(CompetentAuthorityType competentAuthorityTypeRcs) {
		this.competentAuthorityTypeRcs = competentAuthorityTypeRcs;
	}

	public CompetentAuthorityType getCompetentAuthorityTypeCfp() {
		return competentAuthorityTypeCfp;
	}

	public void setCompetentAuthorityTypeCfp(CompetentAuthorityType competentAuthorityTypeCfp) {
		this.competentAuthorityTypeCfp = competentAuthorityTypeCfp;
	}

	public InvoiceStatus getInvoiceStatusSend() {
		return invoiceStatusSend;
	}

	public void setInvoiceStatusSend(InvoiceStatus invoiceStatusSend) {
		this.invoiceStatusSend = invoiceStatusSend;
	}

	public InvoiceStatus getInvoiceStatusPayed() {
		return invoiceStatusPayed;
	}

	public void setInvoiceStatusPayed(InvoiceStatus invoiceStatusPayed) {
		this.invoiceStatusPayed = invoiceStatusPayed;
	}

	public InvoiceStatus getInvoiceStatusCancelled() {
		return invoiceStatusCancelled;
	}

	public void setInvoiceStatusCancelled(InvoiceStatus invoiceStatusCancelled) {
		this.invoiceStatusCancelled = invoiceStatusCancelled;
	}

	public PaymentWay getPaymentWayInbound() {
		return paymentWayInbound;
	}

	public void setPaymentWayInbound(PaymentWay paymentWayInbound) {
		this.paymentWayInbound = paymentWayInbound;
	}

	public PaymentWay getPaymentWayOutboud() {
		return paymentWayOutboud;
	}

	public void setPaymentWayOutboud(PaymentWay paymentWayOutboud) {
		this.paymentWayOutboud = paymentWayOutboud;
	}

	public Vat getVatTwenty() {
		return vatTwenty;
	}

	public void setVatTwenty(Vat vatTwenty) {
		this.vatTwenty = vatTwenty;
	}

	public Vat getVatEight() {
		return vatEight;
	}

	public void setVatEight(Vat vatEight) {
		this.vatEight = vatEight;
	}

	public Department getDepartmentMartinique() {
		return departmentMartinique;
	}

	public void setDepartmentMartinique(Department departmentMartinique) {
		this.departmentMartinique = departmentMartinique;
	}

	public Department getDepartmentGuadeloupe() {
		return departmentGuadeloupe;
	}

	public void setDepartmentGuadeloupe(Department departmentGuadeloupe) {
		this.departmentGuadeloupe = departmentGuadeloupe;
	}

	public Department getDepartmentReunion() {
		return departmentReunion;
	}

	public void setDepartmentReunion(Department departmentReunion) {
		this.departmentReunion = departmentReunion;
	}

	public TypePersonne getTypePersonnePersonnePhysique() {
		return typePersonnePersonnePhysique;
	}

	public void setTypePersonnePersonnePhysique(TypePersonne typePersonnePersonnePhysique) {
		this.typePersonnePersonnePhysique = typePersonnePersonnePhysique;
	}

	public TypePersonne getTypePersonneExploitation() {
		return typePersonneExploitation;
	}

	public void setTypePersonneExploitation(TypePersonne typePersonneExploitation) {
		this.typePersonneExploitation = typePersonneExploitation;
	}

	public TypePersonne getTypePersonnePersonneMorale() {
		return typePersonnePersonneMorale;
	}

	public void setTypePersonnePersonneMorale(TypePersonne typePersonnePersonneMorale) {
		this.typePersonnePersonneMorale = typePersonnePersonneMorale;
	}

	public FormeJuridique getFormeJuridiqueEntrepreneurIndividuel() {
		return formeJuridiqueEntrepreneurIndividuel;
	}

	public void setFormeJuridiqueEntrepreneurIndividuel(FormeJuridique formeJuridiqueEntrepreneurIndividuel) {
		this.formeJuridiqueEntrepreneurIndividuel = formeJuridiqueEntrepreneurIndividuel;
	}

	public TypeFormalite getTypeFormaliteCessation() {
		return typeFormaliteCessation;
	}

	public void setTypeFormaliteCessation(TypeFormalite typeFormaliteCessation) {
		this.typeFormaliteCessation = typeFormaliteCessation;
	}

	public TypeFormalite getTypeFormaliteCorrection() {
		return typeFormaliteCorrection;
	}

	public void setTypeFormaliteCorrection(TypeFormalite typeFormaliteCorrection) {
		this.typeFormaliteCorrection = typeFormaliteCorrection;
	}

	public TypeFormalite getTypeFormaliteModification() {
		return typeFormaliteModification;
	}

	public void setTypeFormaliteModification(TypeFormalite typeFormaliteModification) {
		this.typeFormaliteModification = typeFormaliteModification;
	}

	public TypeFormalite getTypeFormaliteCreation() {
		return typeFormaliteCreation;
	}

	public void setTypeFormaliteCreation(TypeFormalite typeFormaliteCreation) {
		this.typeFormaliteCreation = typeFormaliteCreation;
	}

}
