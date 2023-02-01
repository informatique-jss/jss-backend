package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.PrincipalAccountingAccount;
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
import com.jss.osiris.modules.quotation.model.TransfertFundsType;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeFormalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypePersonne;
import com.jss.osiris.modules.tiers.model.BillingClosureRecipientType;
import com.jss.osiris.modules.tiers.model.BillingClosureType;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.RefundType;
import com.jss.osiris.modules.tiers.model.SubscriptionPeriodType;
import com.jss.osiris.modules.tiers.model.TiersType;

@Entity
public class Constant implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_label_type_code_affaire")
	private BillingLabelType billingLabelTypeCodeAffaire;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_label_type_other")
	private BillingLabelType billingLabelTypeOther;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_label_type_customer")
	private BillingLabelType billingLabelTypeCustomer;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_accounting_journal_sales")
	private AccountingJournal accountingJournalSales;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_accounting_journal_purchases")
	private AccountingJournal accountingJournalPurchases;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_accounting_journal_anouveau")
	private AccountingJournal accountingJournalANouveau;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_accounting_journal_bank")
	private AccountingJournal accountingJournalBank;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_accounting_journal_miscellaneous_operations")
	private AccountingJournal accountingJournalMiscellaneousOperations;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tiers_type_prospect")
	private TiersType tiersTypeProspect;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tiers_type_client")
	private TiersType tiersTypeClient;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_document_type_digital")
	private DocumentType documentTypeDigital;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_document_type_paper")
	private DocumentType documentTypePaper;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_document_type_billing")
	private DocumentType documentTypeBilling;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_document_type_dunning")
	private DocumentType documentTypeDunning;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_document_type_refund")
	private DocumentType documentTypeRefund;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_document_type_billing_closure")
	private DocumentType documentTypeBillingClosure;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_document_type_provisionnal_receipt")
	private DocumentType documentTypeProvisionnalReceipt;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_attachment_type_kbis")
	private AttachmentType attachmentTypeKbis;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_attachment_type_cni")
	private AttachmentType attachmentTypeCni;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_attachment_type_logo")
	private AttachmentType attachmentTypeLogo;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_attachment_type_proof_of_address")
	private AttachmentType attachmentTypeProofOfAddress;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_attachment_type_invoice")
	private AttachmentType attachmentTypeInvoice;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_attachment_type_kbis_updated")
	private AttachmentType attachmentTypeKbisUpdated;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_attachment_type_publication_flag")
	private AttachmentType attachmentTypePublicationFlag;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_attachment_type_publication_receipt")
	private AttachmentType attachmentTypePublicationReceipt;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_attachment_type_publication_proof")
	private AttachmentType attachmentTypePublicationProof;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_attachment_type_journal")
	private AttachmentType attachmentTypeJournal;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_attachment_type_announcement")
	private AttachmentType attachmentTypeAnnouncement;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_attachment_type_billing_closure")
	private AttachmentType attachmentTypeBillingClosure;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_attachment_type_proof_reading")
	private AttachmentType attachmentTypeProofReading;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_attachment_type_automatic_mail")
	private AttachmentType attachmentTypeAutomaticMail;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_country_france")
	private Country countryFrance;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_country_monaco")
	private Country countryMonaco;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_central_pay_fees")
	private BillingType billingTypeCentralPayFees;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_logo")
	private BillingType billingTypeLogo;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_redacted_by_jss")
	private BillingType billingTypeRedactedByJss;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_balo_package")
	private BillingType billingTypeBaloPackage;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_publication_paper")
	private BillingType billingTypePublicationPaper;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_publication_receipt")
	private BillingType billingTypePublicationReceipt;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_publication_flag")
	private BillingType billingTypePublicationFlag;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_bodacc_followup")
	private BillingType billingTypeBodaccFollowup;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_bodacc_followup_redaction")
	private BillingType billingTypeBodaccFollowupAndRedaction;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_nantissement_deposit")
	private BillingType billingTypeNantissementDeposit;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_social_share_nantissement_redaction")
	private BillingType billingTypeSocialShareNantissementRedaction;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_businness_nantissement_redaction")
	private BillingType billingTypeBusinnessNantissementRedaction;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_seller_privilege_redaction")
	private BillingType billingTypeSellerPrivilegeRedaction;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_treatment_multiple_modiciation")
	private BillingType billingTypeTreatmentMultipleModiciation;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_vacation_multiple_modification")
	private BillingType billingTypeVacationMultipleModification;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_register_purchase")
	private BillingType billingTypeRegisterPurchase;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_register_initials")
	private BillingType billingTypeRegisterInitials;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_register_shipping_costs")
	private BillingType billingTypeRegisterShippingCosts;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_disbursement")
	private BillingType billingTypeDisbursement;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_feasibility_study")
	private BillingType billingTypeFeasibilityStudy;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_chronopost_fees")
	private BillingType billingTypeChronopostFees;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_application_fees")
	private BillingType billingTypeApplicationFees;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_bank_cheque")
	private BillingType billingTypeBankCheque;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_complexe_file")
	private BillingType billingTypeComplexeFile;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_bilan")
	private BillingType billingTypeBilan;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_document_scanning")
	private BillingType billingTypeDocumentScanning;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_emergency")
	private BillingType billingTypeEmergency;

	@Column(length = 1000)
	private String stringNantissementDepositFormeJuridiqueCode;
	@Column(length = 1000)
	private String strinSocialShareNantissementRedactionFormeJuridiqueCode;
	@Column(length = 1000)
	private String stringBusinnessNantissementRedactionFormeJuridiqueCode;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_payment_type_prelevement")
	private PaymentType paymentTypePrelevement;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_payment_type_virement")
	private PaymentType paymentTypeVirement;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_payment_type_cb")
	private PaymentType paymentTypeCB;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_payment_type_especes")
	private PaymentType paymentTypeEspeces;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_refund_type_virement")
	private RefundType refundTypeVirement;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_subscription_period_type_12m")
	private SubscriptionPeriodType subscriptionPeriodType12M;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_legal_form_unregistered")
	private LegalForm legalFormUnregistered;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_journal_type_spel")
	private JournalType journalTypeSpel;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_journal_type_paper")
	private JournalType journalTypePaper;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_confrere_jss_spel")
	private Confrere confrereJssSpel;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_domiciliation_constract_type_keep_mail")
	private DomiciliationContractType domiciliationContractTypeKeepMail;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_domiciliation_constract_type_route_mail")
	private DomiciliationContractType domiciliationContractTypeRouteMail;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_domiciliation_constract_type_route_email_mail")
	private DomiciliationContractType domiciliationContractTypeRouteEmailAndMail;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_domiciliation_constract_type_route_email")
	private DomiciliationContractType domiciliationContractTypeRouteEmail;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_mail_redirection_type_other")
	private MailRedirectionType mailRedirectionTypeOther;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_bodacc_publication_type_merging")
	private BodaccPublicationType bodaccPublicationTypeMerging;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_bodacc_publication_type_split")
	private BodaccPublicationType bodaccPublicationTypeSplit;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_bodacc_publication_type_partial_split")
	private BodaccPublicationType bodaccPublicationTypePartialSplit;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_bodacc_publication_type_possession_dispatch")
	private BodaccPublicationType bodaccPublicationTypePossessionDispatch;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_bodacc_publication_type_estate_representative_designation")
	private BodaccPublicationType bodaccPublicationTypeEstateRepresentativeDesignation;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_bodacc_publication_type_sale_of_business")
	private BodaccPublicationType bodaccPublicationTypeSaleOfBusiness;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_act_type_seing")
	private ActType actTypeSeing;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_act_type_authentic")
	private ActType actTypeAuthentic;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_assignation_type_employee")
	private AssignationType assignationTypeEmployee;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_employee_billing_responsible")
	private Employee employeeBillingResponsible;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_employee_mail_responsible")
	private Employee employeeMailResponsible;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_employee_invoice_reminder_responsible")
	private Employee employeeInvoiceReminderResponsible;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_transfert_funds_type_physique")
	private TransfertFundsType transfertFundsTypePhysique;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_transfert_funds_type_moral")
	private TransfertFundsType transfertFundsTypeMoral;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_transfert_funds_type_bail")
	private TransfertFundsType transfertFundsTypeBail;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_competent_authority_type_rcs")
	private CompetentAuthorityType competentAuthorityTypeRcs;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_competent_authority_type_cfp")
	private CompetentAuthorityType competentAuthorityTypeCfp;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_invoice_status_send")
	private InvoiceStatus invoiceStatusSend;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_invoice_status_received")
	private InvoiceStatus invoiceStatusReceived;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_invoice_status_payed")
	private InvoiceStatus invoiceStatusPayed;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_invoice_status_cancelled")
	private InvoiceStatus invoiceStatusCancelled;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_payment_way_inbound")
	private PaymentWay paymentWayInbound;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_payment_way_outbound")
	private PaymentWay paymentWayOutboud;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_vat_twenty")
	private Vat vatTwenty;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_vat_eight")
	private Vat vatEight;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_vat_zero")
	private Vat vatZero;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_vat_deductible")
	private Vat vatDeductible;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_department_martinique")
	private Department departmentMartinique;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_department_guadeloupe")
	private Department departmentGuadeloupe;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_department_reunion")
	private Department departmentReunion;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_type_personne_personne_physique")
	private TypePersonne typePersonnePersonnePhysique;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_type_personne_personne_exploitation")
	private TypePersonne typePersonneExploitation;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_type_personne_personne_morale")
	private TypePersonne typePersonnePersonneMorale;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_type_formalite_cessation")
	private TypeFormalite typeFormaliteCessation;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_type_formalite_correction")
	private TypeFormalite typeFormaliteCorrection;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_type_formalite_modification")
	private TypeFormalite typeFormaliteModification;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_type_formalite_creation")
	private TypeFormalite typeFormaliteCreation;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_employee_sales_director")
	private Employee employeeSalesDirector;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_closure_recipient_type_other")
	private BillingClosureRecipientType billingClosureRecipientTypeOther;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_closure_recipient_type_responsable")
	private BillingClosureRecipientType billingClosureRecipientTypeResponsable;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_closure_recipient_type_client")
	private BillingClosureRecipientType billingClosureRecipientTypeClient;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_closure_type_affaire")
	private BillingClosureType billingClosureTypeAffaire;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_delivery_service_jss")
	private DeliveryService deliveryServiceJss;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_language_french")
	private Language languageFrench;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_principal_accounting_account_customer")
	private PrincipalAccountingAccount principalAccountingAccountCustomer;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_principal_accounting_account_provider")
	private PrincipalAccountingAccount principalAccountingAccountProvider;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_principal_accounting_account_deposit")
	private PrincipalAccountingAccount principalAccountingAccountDeposit;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_principal_accounting_account_product")
	private PrincipalAccountingAccount principalAccountingAccountProduct;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_principal_accounting_account_charge")
	private PrincipalAccountingAccount principalAccountingAccountCharge;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_principal_accounting_account_bank")
	private PrincipalAccountingAccount principalAccountingAccountBank;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_principal_accounting_account_waiting")
	private PrincipalAccountingAccount principalAccountingAccountWaiting;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_principal_accounting_account_lost")
	private PrincipalAccountingAccount principalAccountingAccountLost;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_principal_accounting_account_profit")
	private PrincipalAccountingAccount principalAccountingAccountProfit;

	private String salesSharedMailbox;
	private String accountingSharedMaiblox;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_accounting_account_bank_central_pay")
	private AccountingAccount accountingAccountBankCentralPay;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_accounting_account_bank_jss")
	private AccountingAccount accountingAccountBankJss;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_vacation_deposit_beneficial_owners")
	private BillingType billingTypeVacationDepositBeneficialOwners;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_vacation_update_beneficial_owners")
	private BillingType billingTypeVacationUpdateBeneficialOwners;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_formality_additional_declaration")
	private BillingType billingTypeFormalityAdditionalDeclaration;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_billing_type_correspondence_fees")
	private BillingType billingTypeCorrespondenceFees;



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

	public Employee getEmployeeSalesDirector() {
		return employeeSalesDirector;
	}

	public void setEmployeeSalesDirector(Employee employeeSalesDirector) {
		this.employeeSalesDirector = employeeSalesDirector;
	}

	public BillingType getBillingTypeRedactedByJss() {
		return billingTypeRedactedByJss;
	}

	public void setBillingTypeRedactedByJss(BillingType billingTypeRedactedByJss) {
		this.billingTypeRedactedByJss = billingTypeRedactedByJss;
	}

	public BillingType getBillingTypeBaloPackage() {
		return billingTypeBaloPackage;
	}

	public BillingType getBillingTypePublicationReceipt() {
		return billingTypePublicationReceipt;
	}

	public BillingType getBillingTypePublicationFlag() {
		return billingTypePublicationFlag;
	}

	public BillingType getBillingTypeBodaccFollowup() {
		return billingTypeBodaccFollowup;
	}

	public BillingType getBillingTypeBodaccFollowupAndRedaction() {
		return billingTypeBodaccFollowupAndRedaction;
	}

	public BillingType getBillingTypeNantissementDeposit() {
		return billingTypeNantissementDeposit;
	}

	public BillingType getBillingTypeSocialShareNantissementRedaction() {
		return billingTypeSocialShareNantissementRedaction;
	}

	public BillingType getBillingTypeBusinnessNantissementRedaction() {
		return billingTypeBusinnessNantissementRedaction;
	}

	public BillingType getBillingTypeSellerPrivilegeRedaction() {
		return billingTypeSellerPrivilegeRedaction;
	}

	public BillingType getBillingTypeTreatmentMultipleModiciation() {
		return billingTypeTreatmentMultipleModiciation;
	}

	public BillingType getBillingTypeVacationMultipleModification() {
		return billingTypeVacationMultipleModification;
	}

	public BillingType getBillingTypeRegisterPurchase() {
		return billingTypeRegisterPurchase;
	}

	public BillingType getBillingTypeRegisterInitials() {
		return billingTypeRegisterInitials;
	}

	public BillingType getBillingTypeRegisterShippingCosts() {
		return billingTypeRegisterShippingCosts;
	}

	public BillingType getBillingTypeDisbursement() {
		return billingTypeDisbursement;
	}

	public BillingType getBillingTypeFeasibilityStudy() {
		return billingTypeFeasibilityStudy;
	}

	public BillingType getBillingTypeChronopostFees() {
		return billingTypeChronopostFees;
	}

	public BillingType getBillingTypeBankCheque() {
		return billingTypeBankCheque;
	}

	public BillingType getBillingTypeComplexeFile() {
		return billingTypeComplexeFile;
	}

	public BillingType getBillingTypeDocumentScanning() {
		return billingTypeDocumentScanning;
	}

	public BillingType getBillingTypeEmergency() {
		return billingTypeEmergency;
	}

	public void setBillingTypeBaloPackage(BillingType billingTypeBaloPackage) {
		this.billingTypeBaloPackage = billingTypeBaloPackage;
	}

	public void setBillingTypePublicationReceipt(BillingType billingTypePublicationReceipt) {
		this.billingTypePublicationReceipt = billingTypePublicationReceipt;
	}

	public void setBillingTypePublicationFlag(BillingType billingTypePublicationFlag) {
		this.billingTypePublicationFlag = billingTypePublicationFlag;
	}

	public void setBillingTypeBodaccFollowup(BillingType billingTypeBodaccFollowup) {
		this.billingTypeBodaccFollowup = billingTypeBodaccFollowup;
	}

	public void setBillingTypeBodaccFollowupAndRedaction(BillingType billingTypeBodaccFollowupAndRedaction) {
		this.billingTypeBodaccFollowupAndRedaction = billingTypeBodaccFollowupAndRedaction;
	}

	public void setBillingTypeNantissementDeposit(BillingType billingTypeNantissementDeposit) {
		this.billingTypeNantissementDeposit = billingTypeNantissementDeposit;
	}

	public void setBillingTypeSocialShareNantissementRedaction(
			BillingType billingTypeSocialShareNantissementRedaction) {
		this.billingTypeSocialShareNantissementRedaction = billingTypeSocialShareNantissementRedaction;
	}

	public void setBillingTypeBusinnessNantissementRedaction(BillingType billingTypeBusinnessNantissementRedaction) {
		this.billingTypeBusinnessNantissementRedaction = billingTypeBusinnessNantissementRedaction;
	}

	public void setBillingTypeSellerPrivilegeRedaction(BillingType billingTypeSellerPrivilegeRedaction) {
		this.billingTypeSellerPrivilegeRedaction = billingTypeSellerPrivilegeRedaction;
	}

	public void setBillingTypeTreatmentMultipleModiciation(BillingType billingTypeTreatmentMultipleModiciation) {
		this.billingTypeTreatmentMultipleModiciation = billingTypeTreatmentMultipleModiciation;
	}

	public void setBillingTypeVacationMultipleModification(BillingType billingTypeVacationMultipleModification) {
		this.billingTypeVacationMultipleModification = billingTypeVacationMultipleModification;
	}

	public void setBillingTypeRegisterPurchase(BillingType billingTypeRegisterPurchase) {
		this.billingTypeRegisterPurchase = billingTypeRegisterPurchase;
	}

	public void setBillingTypeRegisterInitials(BillingType billingTypeRegisterInitials) {
		this.billingTypeRegisterInitials = billingTypeRegisterInitials;
	}

	public void setBillingTypeRegisterShippingCosts(BillingType billingTypeRegisterShippingCosts) {
		this.billingTypeRegisterShippingCosts = billingTypeRegisterShippingCosts;
	}

	public void setBillingTypeDisbursement(BillingType billingTypeDisbursement) {
		this.billingTypeDisbursement = billingTypeDisbursement;
	}

	public void setBillingTypeFeasibilityStudy(BillingType billingTypeFeasibilityStudy) {
		this.billingTypeFeasibilityStudy = billingTypeFeasibilityStudy;
	}

	public void setBillingTypeChronopostFees(BillingType billingTypeChronopostFees) {
		this.billingTypeChronopostFees = billingTypeChronopostFees;
	}

	public void setBillingTypeBankCheque(BillingType billingTypeBankCheque) {
		this.billingTypeBankCheque = billingTypeBankCheque;
	}

	public void setBillingTypeComplexeFile(BillingType billingTypeComplexeFile) {
		this.billingTypeComplexeFile = billingTypeComplexeFile;
	}

	public void setBillingTypeDocumentScanning(BillingType billingTypeDocumentScanning) {
		this.billingTypeDocumentScanning = billingTypeDocumentScanning;
	}

	public void setBillingTypeEmergency(BillingType billingTypeEmergency) {
		this.billingTypeEmergency = billingTypeEmergency;
	}

	public JournalType getJournalTypePaper() {
		return journalTypePaper;
	}

	public void setJournalTypePaper(JournalType journalTypePaper) {
		this.journalTypePaper = journalTypePaper;
	}

	public String getStringNantissementDepositFormeJuridiqueCode() {
		return stringNantissementDepositFormeJuridiqueCode;
	}

	public void setStringNantissementDepositFormeJuridiqueCode(String stringNantissementDepositFormeJuridiqueCode) {
		this.stringNantissementDepositFormeJuridiqueCode = stringNantissementDepositFormeJuridiqueCode;
	}

	public String getStrinSocialShareNantissementRedactionFormeJuridiqueCode() {
		return strinSocialShareNantissementRedactionFormeJuridiqueCode;
	}

	public void setStrinSocialShareNantissementRedactionFormeJuridiqueCode(
			String strinSocialShareNantissementRedactionFormeJuridiqueCode) {
		this.strinSocialShareNantissementRedactionFormeJuridiqueCode = strinSocialShareNantissementRedactionFormeJuridiqueCode;
	}

	public String getStringBusinnessNantissementRedactionFormeJuridiqueCode() {
		return stringBusinnessNantissementRedactionFormeJuridiqueCode;
	}

	public void setStringBusinnessNantissementRedactionFormeJuridiqueCode(
			String stringBusinnessNantissementRedactionFormeJuridiqueCode) {
		this.stringBusinnessNantissementRedactionFormeJuridiqueCode = stringBusinnessNantissementRedactionFormeJuridiqueCode;
	}

	public BillingType getBillingTypePublicationPaper() {
		return billingTypePublicationPaper;
	}

	public void setBillingTypePublicationPaper(BillingType billingTypePublicationPaper) {
		this.billingTypePublicationPaper = billingTypePublicationPaper;
	}

	public String getSalesSharedMailbox() {
		return salesSharedMailbox;
	}

	public void setSalesSharedMailbox(String salesSharedMailbox) {
		this.salesSharedMailbox = salesSharedMailbox;
	}

	public String getAccountingSharedMaiblox() {
		return accountingSharedMaiblox;
	}

	public void setAccountingSharedMaiblox(String accountingSharedMaiblox) {
		this.accountingSharedMaiblox = accountingSharedMaiblox;
	}

	public AttachmentType getAttachmentTypeInvoice() {
		return attachmentTypeInvoice;
	}

	public void setAttachmentTypeInvoice(AttachmentType attachmentTypeInvoice) {
		this.attachmentTypeInvoice = attachmentTypeInvoice;
	}

	public AttachmentType getAttachmentTypeKbisUpdated() {
		return attachmentTypeKbisUpdated;
	}

	public void setAttachmentTypeKbisUpdated(AttachmentType attachmentTypeKbisUpdated) {
		this.attachmentTypeKbisUpdated = attachmentTypeKbisUpdated;
	}

	public AttachmentType getAttachmentTypePublicationFlag() {
		return attachmentTypePublicationFlag;
	}

	public void setAttachmentTypePublicationFlag(AttachmentType attachmentTypePublicationFlag) {
		this.attachmentTypePublicationFlag = attachmentTypePublicationFlag;
	}

	public AttachmentType getAttachmentTypePublicationReceipt() {
		return attachmentTypePublicationReceipt;
	}

	public void setAttachmentTypePublicationReceipt(AttachmentType attachmentTypePublicationReceipt) {
		this.attachmentTypePublicationReceipt = attachmentTypePublicationReceipt;
	}

	public Employee getEmployeeInvoiceReminderResponsible() {
		return employeeInvoiceReminderResponsible;
	}

	public void setEmployeeInvoiceReminderResponsible(Employee employeeInvoiceReminderResponsible) {
		this.employeeInvoiceReminderResponsible = employeeInvoiceReminderResponsible;
	}

	public Confrere getConfrereJssSpel() {
		return confrereJssSpel;
	}

	public void setConfrereJssSpel(Confrere confrereJssSpel) {
		this.confrereJssSpel = confrereJssSpel;
	}

	public AttachmentType getAttachmentTypePublicationProof() {
		return attachmentTypePublicationProof;
	}

	public void setAttachmentTypePublicationProof(AttachmentType attachmentTypePublicationProof) {
		this.attachmentTypePublicationProof = attachmentTypePublicationProof;
	}

	public Employee getEmployeeMailResponsible() {
		return employeeMailResponsible;
	}

	public void setEmployeeMailResponsible(Employee employeeMailResponsible) {
		this.employeeMailResponsible = employeeMailResponsible;
	}

	public AttachmentType getAttachmentTypeJournal() {
		return attachmentTypeJournal;
	}

	public void setAttachmentTypeJournal(AttachmentType attachmentTypeJournal) {
		this.attachmentTypeJournal = attachmentTypeJournal;
	}

	public BillingClosureRecipientType getBillingClosureRecipientTypeResponsable() {
		return billingClosureRecipientTypeResponsable;
	}

	public void setBillingClosureRecipientTypeResponsable(
			BillingClosureRecipientType billingClosureRecipientTypeResponsable) {
		this.billingClosureRecipientTypeResponsable = billingClosureRecipientTypeResponsable;
	}

	public BillingClosureRecipientType getBillingClosureRecipientTypeClient() {
		return billingClosureRecipientTypeClient;
	}

	public void setBillingClosureRecipientTypeClient(BillingClosureRecipientType billingClosureRecipientTypeClient) {
		this.billingClosureRecipientTypeClient = billingClosureRecipientTypeClient;
	}

	public TiersType getTiersTypeClient() {
		return tiersTypeClient;
	}

	public void setTiersTypeClient(TiersType tiersTypeClient) {
		this.tiersTypeClient = tiersTypeClient;
	}

	public AttachmentType getAttachmentTypeBillingClosure() {
		return attachmentTypeBillingClosure;
	}

	public void setAttachmentTypeBillingClosure(AttachmentType attachmentTypeBillingClosure) {
		this.attachmentTypeBillingClosure = attachmentTypeBillingClosure;
	}

	public BillingClosureType getBillingClosureTypeAffaire() {
		return billingClosureTypeAffaire;
	}

	public void setBillingClosureTypeAffaire(BillingClosureType billingClosureTypeAffaire) {
		this.billingClosureTypeAffaire = billingClosureTypeAffaire;
	}

	public InvoiceStatus getInvoiceStatusReceived() {
		return invoiceStatusReceived;
	}

	public void setInvoiceStatusReceived(InvoiceStatus invoiceStatusReceived) {
		this.invoiceStatusReceived = invoiceStatusReceived;
	}

	public DeliveryService getDeliveryServiceJss() {
		return deliveryServiceJss;
	}

	public void setDeliveryServiceJss(DeliveryService deliveryServiceJss) {
		this.deliveryServiceJss = deliveryServiceJss;
	}

	public Language getLanguageFrench() {
		return languageFrench;
	}

	public void setLanguageFrench(Language languageFrench) {
		this.languageFrench = languageFrench;
	}

	public BillingType getBillingTypeBilan() {
		return billingTypeBilan;
	}

	public void setBillingTypeBilan(BillingType billingTypeBilan) {
		this.billingTypeBilan = billingTypeBilan;
	}

	public PrincipalAccountingAccount getPrincipalAccountingAccountCustomer() {
		return principalAccountingAccountCustomer;
	}

	public void setPrincipalAccountingAccountCustomer(PrincipalAccountingAccount principalAccountingAccountCustomer) {
		this.principalAccountingAccountCustomer = principalAccountingAccountCustomer;
	}

	public PrincipalAccountingAccount getPrincipalAccountingAccountProvider() {
		return principalAccountingAccountProvider;
	}

	public void setPrincipalAccountingAccountProvider(PrincipalAccountingAccount principalAccountingAccountProvider) {
		this.principalAccountingAccountProvider = principalAccountingAccountProvider;
	}

	public PrincipalAccountingAccount getPrincipalAccountingAccountDeposit() {
		return principalAccountingAccountDeposit;
	}

	public void setPrincipalAccountingAccountDeposit(PrincipalAccountingAccount principalAccountingAccountDeposit) {
		this.principalAccountingAccountDeposit = principalAccountingAccountDeposit;
	}

	public PrincipalAccountingAccount getPrincipalAccountingAccountProduct() {
		return principalAccountingAccountProduct;
	}

	public void setPrincipalAccountingAccountProduct(PrincipalAccountingAccount principalAccountingAccountProduct) {
		this.principalAccountingAccountProduct = principalAccountingAccountProduct;
	}

	public PrincipalAccountingAccount getPrincipalAccountingAccountCharge() {
		return principalAccountingAccountCharge;
	}

	public void setPrincipalAccountingAccountCharge(PrincipalAccountingAccount principalAccountingAccountCharge) {
		this.principalAccountingAccountCharge = principalAccountingAccountCharge;
	}

	public PrincipalAccountingAccount getPrincipalAccountingAccountBank() {
		return principalAccountingAccountBank;
	}

	public void setPrincipalAccountingAccountBank(PrincipalAccountingAccount principalAccountingAccountBank) {
		this.principalAccountingAccountBank = principalAccountingAccountBank;
	}

	public PrincipalAccountingAccount getPrincipalAccountingAccountWaiting() {
		return principalAccountingAccountWaiting;
	}

	public void setPrincipalAccountingAccountWaiting(PrincipalAccountingAccount principalAccountingAccountWaiting) {
		this.principalAccountingAccountWaiting = principalAccountingAccountWaiting;
	}

	public PrincipalAccountingAccount getPrincipalAccountingAccountLost() {
		return principalAccountingAccountLost;
	}

	public void setPrincipalAccountingAccountLost(PrincipalAccountingAccount principalAccountingAccountLost) {
		this.principalAccountingAccountLost = principalAccountingAccountLost;
	}

	public PrincipalAccountingAccount getPrincipalAccountingAccountProfit() {
		return principalAccountingAccountProfit;
	}

	public void setPrincipalAccountingAccountProfit(PrincipalAccountingAccount principalAccountingAccountProfit) {
		this.principalAccountingAccountProfit = principalAccountingAccountProfit;
	}

	public BillingType getBillingTypeApplicationFees() {
		return billingTypeApplicationFees;
	}

	public void setBillingTypeApplicationFees(BillingType billingTypeApplicationFees) {
		this.billingTypeApplicationFees = billingTypeApplicationFees;
	}

	public DocumentType getDocumentTypeDigital() {
		return documentTypeDigital;
	}

	public void setDocumentTypeDigital(DocumentType documentTypeDigital) {
		this.documentTypeDigital = documentTypeDigital;
	}

	public DocumentType getDocumentTypePaper() {
		return documentTypePaper;
	}

	public void setDocumentTypePaper(DocumentType documentTypePaper) {
		this.documentTypePaper = documentTypePaper;
	}

	public AttachmentType getAttachmentTypeProofReading() {
		return attachmentTypeProofReading;
	}

	public void setAttachmentTypeProofReading(AttachmentType attachmentTypeProofReading) {
		this.attachmentTypeProofReading = attachmentTypeProofReading;
	}

	public AttachmentType getAttachmentTypeAutomaticMail() {
		return attachmentTypeAutomaticMail;
	}

	public void setAttachmentTypeAutomaticMail(AttachmentType attachmentTypeAutomaticMail) {
		this.attachmentTypeAutomaticMail = attachmentTypeAutomaticMail;
	}

	public BillingClosureRecipientType getBillingClosureRecipientTypeOther() {
		return billingClosureRecipientTypeOther;
	}

	public void setBillingClosureRecipientTypeOther(BillingClosureRecipientType billingClosureRecipientTypeOther) {
		this.billingClosureRecipientTypeOther = billingClosureRecipientTypeOther;
	}

	public AccountingAccount getAccountingAccountBankCentralPay() {
		return accountingAccountBankCentralPay;
	}

	public void setAccountingAccountBankCentralPay(AccountingAccount accountingAccountBankCentralPay) {
		this.accountingAccountBankCentralPay = accountingAccountBankCentralPay;
	}

	public BillingType getBillingTypeCentralPayFees() {
		return billingTypeCentralPayFees;
	}

	public void setBillingTypeCentralPayFees(BillingType billingTypeCentralPayFees) {
		this.billingTypeCentralPayFees = billingTypeCentralPayFees;
	}

	public AccountingAccount getAccountingAccountBankJss() {
		return accountingAccountBankJss;
	}

	public void setAccountingAccountBankJss(AccountingAccount accountingAccountBankJss) {
		this.accountingAccountBankJss = accountingAccountBankJss;
	}

	public AccountingJournal getAccountingJournalBank() {
		return accountingJournalBank;
	}

	public void setAccountingJournalBank(AccountingJournal accountingJournalBank) {
		this.accountingJournalBank = accountingJournalBank;
	}

	public AccountingJournal getAccountingJournalMiscellaneousOperations() {
		return accountingJournalMiscellaneousOperations;
	}

	public void setAccountingJournalMiscellaneousOperations(
			AccountingJournal accountingJournalMiscellaneousOperations) {
		this.accountingJournalMiscellaneousOperations = accountingJournalMiscellaneousOperations;
	}

	public AttachmentType getAttachmentTypeAnnouncement() {
		return attachmentTypeAnnouncement;
	}

	public void setAttachmentTypeAnnouncement(AttachmentType attachmentTypeAnnouncement) {
		this.attachmentTypeAnnouncement = attachmentTypeAnnouncement;
	}

	public Vat getVatDeductible() {
		return vatDeductible;
	}

	public void setVatDeductible(Vat vatDeductible) {
		this.vatDeductible = vatDeductible;
	}

	public BillingType getBillingTypeVacationDepositBeneficialOwners() {
		return billingTypeVacationDepositBeneficialOwners;
	}

	public void setBillingTypeVacationDepositBeneficialOwners(BillingType billingTypeVacationDepositBeneficialOwners) {
		this.billingTypeVacationDepositBeneficialOwners = billingTypeVacationDepositBeneficialOwners;
	}

	public BillingType getBillingTypeVacationUpdateBeneficialOwners() {
		return billingTypeVacationUpdateBeneficialOwners;
	}

	public void setBillingTypeVacationUpdateBeneficialOwners(BillingType billingTypeVacationUpdateBeneficialOwners) {
		this.billingTypeVacationUpdateBeneficialOwners = billingTypeVacationUpdateBeneficialOwners;
	}

	public BillingType getBillingTypeFormalityAdditionalDeclaration() {
		return billingTypeFormalityAdditionalDeclaration;
	}

	public void setBillingTypeFormalityAdditionalDeclaration(BillingType billingTypeFormalityAdditionalDeclaration) {
		this.billingTypeFormalityAdditionalDeclaration = billingTypeFormalityAdditionalDeclaration;
	}

	public BillingType getBillingTypeCorrespondenceFees() {
		return billingTypeCorrespondenceFees;
	}

	public void setBillingTypeCorrespondenceFees(BillingType billingTypeCorrespondenceFees) {
		this.billingTypeCorrespondenceFees = billingTypeCorrespondenceFees;
	}

	
	public Vat getVatZero() {
		return vatZero;
	}

	public void setVatZero(Vat vatZero) {
		this.vatZero = vatZero;
	}

}
