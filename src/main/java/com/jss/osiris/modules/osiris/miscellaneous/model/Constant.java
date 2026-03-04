package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.myjss.wordpress.model.Category;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccount;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.osiris.accounting.model.AccountingJournal;
import com.jss.osiris.modules.osiris.accounting.model.PrincipalAccountingAccount;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.ActType;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AssignationType;
import com.jss.osiris.modules.osiris.quotation.model.Confrere;
import com.jss.osiris.modules.osiris.quotation.model.DomiciliationContractType;
import com.jss.osiris.modules.osiris.quotation.model.JournalType;
import com.jss.osiris.modules.osiris.quotation.model.MailRedirectionType;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionFamilyType;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionScreenType;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionType;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFamily;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFamilyGroup;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFieldType;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;
import com.jss.osiris.modules.osiris.quotation.model.TransfertFundsType;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDocument;
import com.jss.osiris.modules.osiris.tiers.model.BillingClosureRecipientType;
import com.jss.osiris.modules.osiris.tiers.model.BillingClosureType;
import com.jss.osiris.modules.osiris.tiers.model.BillingLabelType;
import com.jss.osiris.modules.osiris.tiers.model.PaymentDeadlineType;
import com.jss.osiris.modules.osiris.tiers.model.RefundType;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.RffFrequency;
import com.jss.osiris.modules.osiris.tiers.model.SubscriptionPeriodType;
import com.jss.osiris.modules.osiris.tiers.model.TiersCategory;
import com.jss.osiris.modules.osiris.tiers.model.TiersFollowupType;
import com.jss.osiris.modules.osiris.tiers.model.TiersType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Constant implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_billing_label_type_code_affaire")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private BillingLabelType billingLabelTypeCodeAffaire;

	@ManyToOne
	@JoinColumn(name = "id_billing_label_type_other")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private BillingLabelType billingLabelTypeOther;

	@ManyToOne
	@JoinColumn(name = "id_billing_label_type_customer")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private BillingLabelType billingLabelTypeCustomer;

	@ManyToOne
	@JoinColumn(name = "id_accounting_journal_sales")
	private AccountingJournal accountingJournalSales;

	@ManyToOne
	@JoinColumn(name = "id_accounting_journal_situation")
	private AccountingJournal accountingJournalSituation;

	@ManyToOne
	@JoinColumn(name = "id_accounting_journal_purchases")
	private AccountingJournal accountingJournalPurchases;

	@ManyToOne
	@JoinColumn(name = "id_accounting_journal_anouveau")
	private AccountingJournal accountingJournalANouveau;

	@ManyToOne
	@JoinColumn(name = "id_accounting_journal_bank")
	private AccountingJournal accountingJournalBank;

	@ManyToOne
	@JoinColumn(name = "id_accounting_journal_cash")
	private AccountingJournal accountingJournalCash;

	@ManyToOne
	@JoinColumn(name = "id_accounting_journal_bilan")
	private AccountingJournal accountingJournalBilan;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_accounting_journal_salary")
	private AccountingJournal accountingJournalSalary;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_accounting_journal_miscellaneous_operations")
	private AccountingJournal accountingJournalMiscellaneousOperations;

	@ManyToOne
	@JoinColumn(name = "id_tiers_type_prospect")
	private TiersType tiersTypeProspect;

	@ManyToOne
	@JoinColumn(name = "id_tiers_type_client")
	private TiersType tiersTypeClient;

	@ManyToOne
	@JoinColumn(name = "id_document_type_digital")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private DocumentType documentTypeDigital;

	@ManyToOne
	@JoinColumn(name = "id_document_type_paper")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private DocumentType documentTypePaper;

	@ManyToOne
	@JoinColumn(name = "id_document_type_billing")
	@JsonView(JacksonViews.MyJssDetailedView.class)
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
	@JoinColumn(name = "id_document_type_synthesis_rbe_signed")
	private TypeDocument documentTypeSynthesisRbeSigned;

	@ManyToOne
	@JoinColumn(name = "id_document_type_synthesis_rbe_unsigned")
	private TypeDocument documentTypeSynthesisRbeUnsigned;

	@ManyToOne
	@JoinColumn(name = "id_document_type_synthesis_unsigned")
	private TypeDocument documentTypeSynthesisUnsigned;

	@ManyToOne
	@JoinColumn(name = "id_document_type_provisionnal_receipt")
	private DocumentType documentTypeProvisionnalReceipt;

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
	@JoinColumn(name = "id_attachment_type_invoice")
	private AttachmentType attachmentTypeInvoice;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_provider_invoice")
	private AttachmentType attachmentTypeProviderInvoice;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_credit_note")
	private AttachmentType attachmentTypeCreditNote;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_kbis_updated")
	private AttachmentType attachmentTypeKbisUpdated;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_rbe")
	private AttachmentType attachmentTypeRbe;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_deposit_receipt")
	private AttachmentType attachmentTypeDepositReceipt;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_publication_flag")
	private AttachmentType attachmentTypePublicationFlag;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_publication_receipt")
	private AttachmentType attachmentTypePublicationReceipt;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_publication_proof")
	private AttachmentType attachmentTypePublicationProof;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_journal")
	private AttachmentType attachmentTypeJournal;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_announcement")
	private AttachmentType attachmentTypeAnnouncement;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_complex_announcement")
	private AttachmentType attachmentTypeComplexAnnouncement;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_billing_closure")
	private AttachmentType attachmentTypeBillingClosure;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_proof_reading")
	private AttachmentType attachmentTypeProofReading;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_automatic_mail")
	private AttachmentType attachmentTypeAutomaticMail;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_contract")
	private AttachmentType attachmentTypeContract;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_template")
	private AttachmentType attachmentTypeTemplate;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_quotation")
	private AttachmentType attachmentTypeQuotation;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_Refus_infogreffe")
	private AttachmentType attachmentTypeRefusInfogreffe;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_Autre_infogreffe")
	private AttachmentType attachmentTypeAutreInfogreffe;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_client_communication")
	private AttachmentType attachmentTypeClientCommunication;

	@ManyToOne
	@JsonView(JacksonViews.MyJssDetailedView.class)
	@JoinColumn(name = "id_attachment_type_application_cv")
	private AttachmentType attachmentTypeApplicationCv;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_purchase_order")
	private AttachmentType attachmentTypePurchaseOrder;

	@ManyToOne
	@JsonView(JacksonViews.MyJssDetailedView.class)
	@JoinColumn(name = "id_country_france")
	private Country countryFrance;

	@ManyToOne
	@JoinColumn(name = "id_country_monaco")
	private Country countryMonaco;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_debours_non_taxable")
	private BillingType billingTypeDeboursNonTaxable;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_central_pay_fees")
	private BillingType billingTypeCentralPayFees;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_logo")
	private BillingType billingTypeLogo;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_redacted_by_jss")
	private BillingType billingTypeRedactedByJss;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_redacted_by_jss_simple")
	private BillingType billingTypeRedactedByJssSimple;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_redacted_by_jss_complex")
	private BillingType billingTypeRedactedByJssComplex;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_redacted_by_jss_free")
	private BillingType billingTypeRedactedByJssFree;

	@ManyToOne
	@JoinColumn(name = "id_payment_deadline_type")
	private PaymentDeadlineType paymentDeadLineType30;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_balo_package")
	private BillingType billingTypeBaloPackage;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_balo_normalization")
	private BillingType billingTypeBaloNormalization;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_balo_publication_flag")
	private BillingType billingTypeBaloPublicationFlag;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_publication_paper")
	private BillingType billingTypePublicationPaper;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_publication_receipt")
	private BillingType billingTypePublicationReceipt;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_publication_flag")
	private BillingType billingTypePublicationFlag;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_nantissement_deposit")
	private BillingType billingTypeNantissementDeposit;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_social_share_nantissement_redaction")
	private BillingType billingTypeSocialShareNantissementRedaction;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_businness_nantissement_redaction")
	private BillingType billingTypeBusinnessNantissementRedaction;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_seller_privilege_redaction")
	private BillingType billingTypeSellerPrivilegeRedaction;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_treatment_multiple_modiciation")
	private BillingType billingTypeTreatmentMultipleModiciation;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_vacation_multiple_modification")
	private BillingType billingTypeVacationMultipleModification;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_register_purchase")
	private BillingType billingTypeRegisterPurchase;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_register_initials")
	private BillingType billingTypeRegisterInitials;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_register_shipping_costs")
	private BillingType billingTypeRegisterShippingCosts;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_disbursement")
	private BillingType billingTypeDisbursement;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_feasibility_study")
	private BillingType billingTypeFeasibilityStudy;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_chronopost_fees")
	private BillingType billingTypeChronopostFees;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_shipping_costs")
	private BillingType billingTypeShippingCosts;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_confrere_fees")
	private BillingType billingTypeConfrereFees;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_application_fees")
	private BillingType billingTypeApplicationFees;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_bank_cheque")
	private BillingType billingTypeBankCheque;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_complexe_file")
	private BillingType billingTypeComplexeFile;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_bilan")
	private BillingType billingTypeBilan;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_infogreffe_debour")
	private BillingType billingTypeInfogreffeDebour;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_emoluments_de_greffe_debour")
	private BillingType billingTypeEmolumentsDeGreffeDebour;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_document_scanning")
	private BillingType billingTypeDocumentScanning;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_emergency")
	private BillingType billingTypeEmergency;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_rne_update")
	private BillingType billingTypeRneUpdate;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_domiciliation_contract_keep_mail")
	private BillingType billingTypeDomiciliationContractTypeKeepMail;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_domiciliation_contract_route_email")
	private BillingType billingTypeDomiciliationContractTypeRouteEmail;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_domiciliation_contract_route_mail")
	private BillingType billingTypeDomiciliationContractTypeRouteMail;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_domiciliation_contract_route_mail_email")
	private BillingType billingTypeDomiciliationContractTypeRouteEmailAndMail;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_supply_full_be_copy")
	private BillingType billingTypeSupplyFullBeCopy;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_kbis")
	private BillingType billingTypeKbis;

	@Column(length = 1000)
	private String stringNantissementDepositFormeJuridiqueCode;
	@Column(length = 1000)
	private String strinSocialShareNantissementRedactionFormeJuridiqueCode;
	@Column(length = 1000)
	private String stringBusinnessNantissementRedactionFormeJuridiqueCode;

	@ManyToOne
	@JoinColumn(name = "id_payment_type_prelevement")
	private PaymentType paymentTypePrelevement;

	@ManyToOne
	@JoinColumn(name = "id_payment_type_virement")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private PaymentType paymentTypeVirement;

	@ManyToOne
	@JoinColumn(name = "id_payment_type_cb")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private PaymentType paymentTypeCB;

	@ManyToOne
	@JoinColumn(name = "id_payment_type_especes")
	private PaymentType paymentTypeEspeces;

	@ManyToOne
	@JoinColumn(name = "id_payment_type_cheques")
	private PaymentType paymentTypeCheques;

	@ManyToOne
	@JoinColumn(name = "id_payment_type_account")
	private PaymentType paymentTypeAccount;

	@ManyToOne
	@JoinColumn(name = "id_refund_type_virement")
	@JsonView(JacksonViews.MyJssDetailedView.class)
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
	@JoinColumn(name = "id_journal_type_paper")
	private JournalType journalTypePaper;

	@ManyToOne
	@JoinColumn(name = "id_confrere_jss_spel")
	private Confrere confrereJssSpel;

	@ManyToOne
	@JoinColumn(name = "id_domiciliation_constract_type_keep_mail")
	private DomiciliationContractType domiciliationContractTypeKeepMail;

	@ManyToOne
	@JoinColumn(name = "id_domiciliation_constract_type_route_mail")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private DomiciliationContractType domiciliationContractTypeRouteMail;

	@ManyToOne
	@JoinColumn(name = "id_domiciliation_constract_type_route_email_mail")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private DomiciliationContractType domiciliationContractTypeRouteEmailAndMail;

	@ManyToOne
	@JoinColumn(name = "id_domiciliation_constract_type_route_email")
	private DomiciliationContractType domiciliationContractTypeRouteEmail;

	@ManyToOne
	@JoinColumn(name = "id_mail_redirection_type_other")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private MailRedirectionType mailRedirectionTypeOther;

	@ManyToOne
	@JoinColumn(name = "id_mail_redirection_type_legal_guardian")
	private MailRedirectionType mailRedirectionTypeLegalGuardian;

	@ManyToOne
	@JoinColumn(name = "id_mail_redirection_type_activity")
	private MailRedirectionType mailRedirectionTypeActivity;

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
	@JoinColumn(name = "id_assignation_type_formaliste")
	private AssignationType assignationTypeFormaliste;

	@ManyToOne
	@JoinColumn(name = "id_assignation_type_publisciste")
	private AssignationType assignationTypePublisciste;

	@ManyToOne
	@JoinColumn(name = "id_employee_billing_responsible")
	private Employee employeeBillingResponsible;

	@ManyToOne
	@JoinColumn(name = "id_employee_production_director")
	private Employee employeeProductionDirector;

	@ManyToOne
	@JoinColumn(name = "id_employee_mail_responsible")
	private Employee employeeMailResponsible;

	@ManyToOne
	@JoinColumn(name = "id_employee_invoice_reminder_responsible")
	private Employee employeeInvoiceReminderResponsible;

	@ManyToOne
	@JoinColumn(name = "id_employee_candidacy_responsible")
	private Employee employeeCandidacyResponsible;

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
	@JoinColumn(name = "id_competent_authority_infogreffe")
	private CompetentAuthority competentAuthorityInfogreffe;

	@ManyToOne
	@JoinColumn(name = "id_competent_authority_inpi")
	private CompetentAuthority competentAuthorityInpi;

	@ManyToOne
	@JoinColumn(name = "id_competent_authority_type_rcs")
	private CompetentAuthorityType competentAuthorityTypeRcs;

	@ManyToOne
	@JoinColumn(name = "id_competent_authority_type_cfp")
	private CompetentAuthorityType competentAuthorityTypeCfp;

	@ManyToOne
	@JoinColumn(name = "id_competent_authority_type_cci")
	private CompetentAuthorityType competentAuthorityTypeCci;

	@ManyToOne
	@JoinColumn(name = "id_competent_authority_type_chambre_metier")
	private CompetentAuthorityType competentAuthorityTypeChambreMetier;

	@ManyToOne
	@JoinColumn(name = "id_competent_authority_type_chambre_agriculturemetier")
	private CompetentAuthorityType competentAuthorityTypeChambreAgriculture;

	@ManyToOne
	@JoinColumn(name = "id_competent_authority_type_urssaf")
	private CompetentAuthorityType competentAuthorityTypeUrssaf;

	@ManyToOne
	@JoinColumn(name = "id_competent_authority_type_direccte")
	private CompetentAuthorityType competentAuthorityTypeDireccte;

	@ManyToOne
	@JoinColumn(name = "id_competent_authority_type_prefecture")
	private CompetentAuthorityType competentAuthorityTypePrefecture;

	@ManyToOne
	@JoinColumn(name = "id_competent_authority_type_spfe")
	private CompetentAuthorityType competentAuthorityTypeSpfe;

	@ManyToOne
	@JoinColumn(name = "id_competent_authority_type_insee")
	private CompetentAuthorityType competentAuthorityTypeInsee;

	@ManyToOne
	@JoinColumn(name = "id_invoice_status_send")
	private InvoiceStatus invoiceStatusSend;

	@ManyToOne
	@JoinColumn(name = "id_invoice_status_received")
	private InvoiceStatus invoiceStatusReceived;

	@ManyToOne
	@JoinColumn(name = "id_invoice_status_payed")
	private InvoiceStatus invoiceStatusPayed;

	@ManyToOne
	@JoinColumn(name = "id_invoice_status_cancelled")
	private InvoiceStatus invoiceStatusCancelled;

	@ManyToOne
	@JoinColumn(name = "id_invoice_status_credit_note_emited")
	private InvoiceStatus invoiceStatusCreditNoteEmited;

	@ManyToOne
	@JoinColumn(name = "id_invoice_status_credit_note_received")
	private InvoiceStatus invoiceStatusCreditNoteReceived;

	@ManyToOne
	@JoinColumn(name = "id_vat_twenty")
	private Vat vatTwenty;

	@ManyToOne
	@JoinColumn(name = "id_vat_two")
	private Vat vatTwo;

	@ManyToOne
	@JoinColumn(name = "id_vat_zero")
	private Vat vatZero;

	@ManyToOne
	@JoinColumn(name = "id_vat_deductible")
	private Vat vatDeductible;

	@ManyToOne
	@JoinColumn(name = "id_vat_deductible_two")
	private Vat vatDeductibleTwo;

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
	@JsonView(JacksonViews.MyJssDetailedView.class)
	@JoinColumn(name = "id_publishing_department_idf")
	private PublishingDepartment publishingDepartmentIdf;

	@ManyToOne
	@JoinColumn(name = "id_employee_sales_director")
	private Employee employeeSalesDirector;

	@ManyToOne
	@JoinColumn(name = "id_billing_closure_recipient_type_other")
	private BillingClosureRecipientType billingClosureRecipientTypeOther;

	@ManyToOne
	@JoinColumn(name = "id_billing_closure_recipient_type_responsable")
	private BillingClosureRecipientType billingClosureRecipientTypeResponsable;

	@ManyToOne
	@JoinColumn(name = "id_billing_closure_recipient_type_client")
	private BillingClosureRecipientType billingClosureRecipientTypeClient;

	@ManyToOne
	@JoinColumn(name = "id_billing_closure_type_affaire")
	private BillingClosureType billingClosureTypeAffaire;

	@ManyToOne
	@JoinColumn(name = "id_delivery_service_jss")
	private DeliveryService deliveryServiceJss;

	@ManyToOne
	@JsonView(JacksonViews.MyJssDetailedView.class)
	@JoinColumn(name = "id_language_french")
	private Language languageFrench;

	@ManyToOne
	@JoinColumn(name = "id_principal_accounting_account_customer")
	private PrincipalAccountingAccount principalAccountingAccountCustomer;

	@ManyToOne
	@JoinColumn(name = "id_principal_accounting_account_provider")
	private PrincipalAccountingAccount principalAccountingAccountProvider;

	@ManyToOne
	@JoinColumn(name = "id_principal_accounting_account_deposit")
	private PrincipalAccountingAccount principalAccountingAccountDeposit;

	@ManyToOne
	@JoinColumn(name = "id_principal_accounting_account_litigious")
	private PrincipalAccountingAccount principalAccountingAccountLitigious;

	@ManyToOne
	@JoinColumn(name = "id_principal_accounting_account_suspicious")
	private PrincipalAccountingAccount principalAccountingAccountSuspicious;

	@ManyToOne
	@JoinColumn(name = "id_principal_accounting_account_deposit_provider")
	private PrincipalAccountingAccount principalAccountingAccountDepositProvider;

	@ManyToOne
	@JoinColumn(name = "id_principal_accounting_account_product")
	private PrincipalAccountingAccount principalAccountingAccountProduct;

	@ManyToOne
	@JoinColumn(name = "id_principal_accounting_account_charge")
	private PrincipalAccountingAccount principalAccountingAccountCharge;

	@ManyToOne
	@JoinColumn(name = "id_principal_accounting_account_bank")
	private PrincipalAccountingAccount principalAccountingAccountBank;

	@ManyToOne
	@JoinColumn(name = "id_principal_accounting_account_waiting")
	private PrincipalAccountingAccount principalAccountingAccountWaiting;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_lost")
	private AccountingAccount accountingAccountLost;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_profit")
	private AccountingAccount accountingAccountProfit;

	private String salesSharedMailbox;
	private String accountingSharedMaiblox;
	private String recoverySharedMaiblox;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_bank_central_pay")
	private AccountingAccount accountingAccountBankCentralPay;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_bank_jss")
	private AccountingAccount accountingAccountBankJss;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_caisse")
	private AccountingAccount accountingAccountCaisse;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_vacation_update_beneficial_owners")
	private BillingType billingTypeVacationUpdateBeneficialOwners;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_formality_additional_declaration")
	private BillingType billingTypeFormalityAdditionalDeclaration;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_correspondence_fees")
	private BillingType billingTypeCorrespondenceFees;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_rff")
	private BillingType billingTypeRff;

	@ManyToOne
	@JoinColumn(name = "id_customer_order_origin_website")
	private CustomerOrderOrigin customerOrderOriginWebSite;

	@ManyToOne
	@JoinColumn(name = "id_customer_order_origin_myjss")
	private CustomerOrderOrigin customerOrderOriginMyJss;

	@ManyToOne
	@JoinColumn(name = "id_customer_order_origin_osiris")
	private CustomerOrderOrigin customerOrderOriginOsiris;

	@ManyToOne
	@JoinColumn(name = "id_provider_central_pay")
	private Provider providerCentralPay;

	@ManyToOne
	@JoinColumn(name = "id_tiers_followup_type_invoice_reminder")
	private TiersFollowupType tiersFollowupTypeInvoiceReminder;

	@ManyToOne
	@JoinColumn(name = "id_tiers_category_presse")
	private TiersCategory tiersCategoryPresse;

	@ManyToOne
	@JoinColumn(name = "id_rff_frequency_annual")
	private RffFrequency rffFrequencyAnnual;

	@ManyToOne
	@JoinColumn(name = "id_rff_frequency_quarterly")
	private RffFrequency rffFrequencyQuarterly;

	@ManyToOne
	@JoinColumn(name = "id_rff_frequency_monthly")
	private RffFrequency rffFrequencyMonthly;

	@ManyToOne
	@JoinColumn(name = "id_customer_order_frequency_annual")
	private CustomerOrderFrequency customerOrderFrequencyAnnual;

	@ManyToOne
	@JoinColumn(name = "id_customer_order_frequency_quarterly")
	private CustomerOrderFrequency customerOrderFrequencyQuarterly;

	@ManyToOne
	@JoinColumn(name = "id_customer_order_frequency_monthly")
	private CustomerOrderFrequency customerOrderFrequencyMonthly;

	@ManyToOne
	@JoinColumn(name = "id_service_type_other")
	private ServiceType serviceTypeOther;

	@ManyToOne
	@JoinColumn(name = "id_service_type_annual_subscription")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private ServiceType serviceTypeAnnualSubscription;

	@ManyToOne
	@JoinColumn(name = "id_service_type_enterprise_annual_subscription")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private ServiceType serviceTypeEnterpriseAnnualSubscription;

	@ManyToOne
	@JoinColumn(name = "id_service_type_monthly_subscription")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private ServiceType serviceTypeMonthlySubscription;

	@ManyToOne
	@JoinColumn(name = "id_service_type_kiosk_newspaper_buy")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private ServiceType serviceTypeKioskNewspaperBuy;

	@ManyToOne
	@JoinColumn(name = "id_service_type_unique_article_buy")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private ServiceType serviceTypeUniqueArticleBuy;

	@ManyToOne
	@JoinColumn(name = "id_special_offer_jss_subscription_reduction")
	private SpecialOffer specialOfferJssSubscriptionReduction;

	@ManyToOne
	@JoinColumn(name = "id_service_type_secondary_center_opening")
	private ServiceType serviceTypeSecondaryCenterOpeningAlAndFormality;

	@ManyToOne
	@JoinColumn(name = "id_provision_type_bilan_publication")
	private ProvisionType provisionTypeBilanPublication;

	@ManyToOne
	@JoinColumn(name = "id_provision_type_registration_act")
	private ProvisionType provisionTypeRegistrationAct;

	@ManyToOne
	@JoinColumn(name = "id_provision_type_rbe")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private ProvisionType provisionTypeRbe;

	@ManyToOne
	@JoinColumn(name = "id_provision_type_character_announcement")
	private ProvisionType provisionTypeCharacterAnnouncement;

	@ManyToOne
	@JoinColumn(name = "id_provision_family_type_deposit")
	private ProvisionFamilyType provisionFamilyTypeDeposit;

	@ManyToOne
	@JoinColumn(name = "id_provision_family_type_bodacc")
	private ProvisionFamilyType provisionFamilyTypeBodacc;

	@ManyToOne
	@JoinColumn(name = "id_provision_family_type_balo")
	private ProvisionFamilyType provisionFamilyTypeBalo;

	@ManyToOne
	@JoinColumn(name = "id_provision_family_type_abonnement")
	private ProvisionFamilyType provisionFamilyTypeAbonnement;

	@ManyToOne
	@JoinColumn(name = "id_active_directory_group_formalites")
	private ActiveDirectoryGroup activeDirectoryGroupFormalites;

	@ManyToOne
	@JoinColumn(name = "id_active_directory_group_direction")
	@JsonView(JacksonViews.OsirisDetailedView.class)
	private ActiveDirectoryGroup activeDirectoryGroupDirection;

	@ManyToOne
	@JoinColumn(name = "id_active_directory_group_insertions")
	private ActiveDirectoryGroup activeDirectoryGroupInsertions;

	@ManyToOne
	@JoinColumn(name = "id_active_directory_group_facturation")
	private ActiveDirectoryGroup activeDirectoryGroupFacturation;

	@ManyToOne
	@JoinColumn(name = "id_active_directory_group_sales")
	@JsonView(JacksonViews.OsirisDetailedView.class)
	private ActiveDirectoryGroup activeDirectoryGroupSales;

	private LocalDate dateAccountingClosureForAccountant;
	private LocalDate dateAccountingClosureForAll;

	@ManyToOne
	@JoinColumn(name = "id_further_information_service_field_type")
	private ServiceFieldType furtherInformationServiceFieldType;

	@ManyToOne
	@JoinColumn(name = "id_responsable_dummy_customer_france")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Responsable responsableDummyCustomerFrance;

	@ManyToOne
	@JoinColumn(name = "id_affaire_dummy_for_subscription")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Affaire affaireDummyForSubscription;

	@ManyToOne
	@JoinColumn(name = "id_provision_screen_type_announcement")
	private ProvisionScreenType provisionScreenTypeAnnouncement;

	@ManyToOne
	@JoinColumn(name = "id_category_interview")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Category categoryInterview;

	@ManyToOne
	@JoinColumn(name = "id_category_podcast")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Category categoryPodcast;

	@ManyToOne
	@JoinColumn(name = "id_category_article")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Category categoryArticle;

	@ManyToOne
	@JoinColumn(name = "id_category_serie")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Category categorySerie;

	@ManyToOne
	@JsonView(JacksonViews.MyJssDetailedView.class)
	@JoinColumn(name = "id_category_exclusivity")
	private Category categoryExclusivity;

	@ManyToOne
	@JsonView(JacksonViews.MyJssDetailedView.class)
	@JoinColumn(name = "id_myjss_category_announcement")
	private MyJssCategory myJssCategoryAnnouncement;

	@ManyToOne
	@JsonView(JacksonViews.MyJssDetailedView.class)
	@JoinColumn(name = "id_myjss_category_formality")
	private MyJssCategory myJssCategoryFormality;

	@ManyToOne
	@JsonView(JacksonViews.MyJssDetailedView.class)
	@JoinColumn(name = "id_myjss_category_domiciliation")
	private MyJssCategory myJssCategoryDomiciliation;

	@ManyToOne
	@JsonView(JacksonViews.MyJssDetailedView.class)
	@JoinColumn(name = "id_myjss_category_apostille")
	private MyJssCategory myJssCategoryApostille;

	@ManyToOne
	@JsonView(JacksonViews.MyJssDetailedView.class)
	@JoinColumn(name = "id_myjss_category_document")
	private MyJssCategory myJssCategoryDocument;

	@ManyToOne
	@JsonView(JacksonViews.MyJssDetailedView.class)
	@JoinColumn(name = "id_jss_category_homepage_first_highlighted")
	private JssCategory jssCategoryHomepageFirstHighlighted;

	@ManyToOne
	@JsonView(JacksonViews.MyJssDetailedView.class)
	@JoinColumn(name = "id_jss_category_homepage_second_highlighted")
	private JssCategory jssCategoryHomepageSecondHighlighted;

	@ManyToOne
	@JsonView(JacksonViews.MyJssDetailedView.class)
	@JoinColumn(name = "id_jss_category_homepage_third_highlighted")
	private JssCategory jssCategoryHomepageThirdHighlighted;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_class_product")
	private AccountingAccountClass accountingAccountClassProduct;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_class_tiers")
	private AccountingAccountClass accountingAccountClassTiers;

	@ManyToOne
	@JoinColumn(name = "id_service_family_immatriculation_al_and_formality")
	private ServiceFamily serviceFamilyImmatriculationAlAndFormality;

	@ManyToOne
	@JoinColumn(name = "id_service_family_group_announcement")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private ServiceFamilyGroup serviceFamilyGroupAnnouncement;

	@ManyToOne
	@JoinColumn(name = "id_service_family_group_other")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private ServiceFamilyGroup serviceFamilyGroupOther;

	@ManyToOne
	@JoinColumn(name = "id_service_family_group_formality")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private ServiceFamilyGroup serviceFamilyGroupFormality;

	private String stringMyJssDemoRequestMail;
	private String stringMyJssWebinarRequestMail;
	private String stringMyJssContactFormRequestMail;

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

	public Vat getVatTwenty() {
		return vatTwenty;
	}

	public void setVatTwenty(Vat vatTwenty) {
		this.vatTwenty = vatTwenty;
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

	public AttachmentType getAttachmentTypeQuotation() {
		return attachmentTypeQuotation;
	}

	public void setAttachmentTypeQuotation(AttachmentType attachmentTypeQuotation) {
		this.attachmentTypeQuotation = attachmentTypeQuotation;
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

	public PrincipalAccountingAccount getPrincipalAccountingAccountLitigious() {
		return principalAccountingAccountLitigious;
	}

	public PrincipalAccountingAccount getPrincipalAccountingAccountSuspicious() {
		return principalAccountingAccountSuspicious;
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

	public InvoiceStatus getInvoiceStatusCreditNoteEmited() {
		return invoiceStatusCreditNoteEmited;
	}

	public void setInvoiceStatusCreditNoteEmited(InvoiceStatus invoiceStatusCreditNoteEmited) {
		this.invoiceStatusCreditNoteEmited = invoiceStatusCreditNoteEmited;
	}

	public AttachmentType getAttachmentTypeCreditNote() {
		return attachmentTypeCreditNote;
	}

	public void setAttachmentTypeCreditNote(AttachmentType attachmentTypeCreditNote) {
		this.attachmentTypeCreditNote = attachmentTypeCreditNote;
	}

	public CompetentAuthorityType getCompetentAuthorityTypeCci() {
		return competentAuthorityTypeCci;
	}

	public void setCompetentAuthorityTypeCci(CompetentAuthorityType competentAuthorityTypeCci) {
		this.competentAuthorityTypeCci = competentAuthorityTypeCci;
	}

	public CompetentAuthorityType getCompetentAuthorityTypeChambreMetier() {
		return competentAuthorityTypeChambreMetier;
	}

	public void setCompetentAuthorityTypeChambreMetier(CompetentAuthorityType competentAuthorityTypeChambreMetier) {
		this.competentAuthorityTypeChambreMetier = competentAuthorityTypeChambreMetier;
	}

	public CompetentAuthorityType getCompetentAuthorityTypeDireccte() {
		return competentAuthorityTypeDireccte;
	}

	public void setCompetentAuthorityTypeDireccte(CompetentAuthorityType competentAuthorityTypeDireccte) {
		this.competentAuthorityTypeDireccte = competentAuthorityTypeDireccte;
	}

	public PaymentType getPaymentTypeCheques() {
		return paymentTypeCheques;
	}

	public void setPaymentTypeCheques(PaymentType paymentTypeCheques) {
		this.paymentTypeCheques = paymentTypeCheques;
	}

	public BillingType getBillingTypeDeboursNonTaxable() {
		return billingTypeDeboursNonTaxable;
	}

	public void setBillingTypeDeboursNonTaxable(BillingType billingTypeDeboursNonTaxable) {
		this.billingTypeDeboursNonTaxable = billingTypeDeboursNonTaxable;
	}

	public BillingType getBillingTypeSupplyFullBeCopy() {
		return billingTypeSupplyFullBeCopy;
	}

	public void setBillingTypeSupplyFullBeCopy(BillingType billingTypeSupplyFullBeCopy) {
		this.billingTypeSupplyFullBeCopy = billingTypeSupplyFullBeCopy;
	}

	public void setPrincipalAccountingAccountLitigious(PrincipalAccountingAccount principalAccountingAccountLitigious) {
		this.principalAccountingAccountLitigious = principalAccountingAccountLitigious;
	}

	public void setPrincipalAccountingAccountSuspicious(
			PrincipalAccountingAccount principalAccountingAccountSuspicious) {
		this.principalAccountingAccountSuspicious = principalAccountingAccountSuspicious;
	}

	public AccountingAccount getAccountingAccountCaisse() {
		return accountingAccountCaisse;
	}

	public void setAccountingAccountCaisse(AccountingAccount accountingAccountCaisse) {
		this.accountingAccountCaisse = accountingAccountCaisse;
	}

	public PaymentType getPaymentTypeAccount() {
		return paymentTypeAccount;
	}

	public void setPaymentTypeAccount(PaymentType paymentTypeAccount) {
		this.paymentTypeAccount = paymentTypeAccount;
	}

	public AccountingJournal getAccountingJournalCash() {
		return accountingJournalCash;
	}

	public void setAccountingJournalCash(AccountingJournal accountingJournalCash) {
		this.accountingJournalCash = accountingJournalCash;
	}

	public PrincipalAccountingAccount getPrincipalAccountingAccountDepositProvider() {
		return principalAccountingAccountDepositProvider;
	}

	public void setPrincipalAccountingAccountDepositProvider(
			PrincipalAccountingAccount principalAccountingAccountDepositProvider) {
		this.principalAccountingAccountDepositProvider = principalAccountingAccountDepositProvider;
	}

	public BillingType getBillingTypeConfrereFees() {
		return billingTypeConfrereFees;
	}

	public void setBillingTypeConfrereFees(BillingType billingTypeConfrereFees) {
		this.billingTypeConfrereFees = billingTypeConfrereFees;
	}

	public BillingType getBillingTypeShippingCosts() {
		return billingTypeShippingCosts;
	}

	public void setBillingTypeShippingCosts(BillingType billingTypeShippingCosts) {
		this.billingTypeShippingCosts = billingTypeShippingCosts;
	}

	public BillingType getBillingTypeInfogreffeDebour() {
		return billingTypeInfogreffeDebour;
	}

	public void setBillingTypeInfogreffeDebour(BillingType billingTypeInfogreffeDebour) {
		this.billingTypeInfogreffeDebour = billingTypeInfogreffeDebour;
	}

	public CompetentAuthority getCompetentAuthorityInfogreffe() {
		return competentAuthorityInfogreffe;
	}

	public void setCompetentAuthorityInfogreffe(CompetentAuthority competentAuthorityInfogreffe) {
		this.competentAuthorityInfogreffe = competentAuthorityInfogreffe;
	}

	public CompetentAuthorityType getCompetentAuthorityTypePrefecture() {
		return competentAuthorityTypePrefecture;
	}

	public void setCompetentAuthorityTypePrefecture(CompetentAuthorityType competentAuthorityTypePrefecture) {
		this.competentAuthorityTypePrefecture = competentAuthorityTypePrefecture;
	}

	public BillingType getBillingTypeBaloPublicationFlag() {
		return billingTypeBaloPublicationFlag;
	}

	public void setBillingTypeBaloPublicationFlag(BillingType billingTypeBaloPublicationFlag) {
		this.billingTypeBaloPublicationFlag = billingTypeBaloPublicationFlag;
	}

	public BillingType getBillingTypeBaloNormalization() {
		return billingTypeBaloNormalization;
	}

	public void setBillingTypeBaloNormalization(BillingType billingTypeBaloNormalization) {
		this.billingTypeBaloNormalization = billingTypeBaloNormalization;
	}

	public InvoiceStatus getInvoiceStatusCreditNoteReceived() {
		return invoiceStatusCreditNoteReceived;
	}

	public void setInvoiceStatusCreditNoteReceived(InvoiceStatus invoiceStatusCreditNoteReceived) {
		this.invoiceStatusCreditNoteReceived = invoiceStatusCreditNoteReceived;
	}

	public AttachmentType getAttachmentTypeRbe() {
		return attachmentTypeRbe;
	}

	public void setAttachmentTypeRbe(AttachmentType attachmentTypeRbe) {
		this.attachmentTypeRbe = attachmentTypeRbe;
	}

	public AttachmentType getAttachmentTypeDepositReceipt() {
		return attachmentTypeDepositReceipt;
	}

	public void setAttachmentTypeDepositReceipt(AttachmentType attachmentTypeDepositReceipt) {
		this.attachmentTypeDepositReceipt = attachmentTypeDepositReceipt;
	}

	public BillingType getBillingTypeEmolumentsDeGreffeDebour() {
		return billingTypeEmolumentsDeGreffeDebour;
	}

	public void setBillingTypeEmolumentsDeGreffeDebour(BillingType billingTypeEmolumentsDeGreffeDebour) {
		this.billingTypeEmolumentsDeGreffeDebour = billingTypeEmolumentsDeGreffeDebour;
	}

	public CompetentAuthority getCompetentAuthorityInpi() {
		return competentAuthorityInpi;
	}

	public void setCompetentAuthorityInpi(CompetentAuthority competentAuthorityInpi) {
		this.competentAuthorityInpi = competentAuthorityInpi;
	}

	public AttachmentType getAttachmentTypeComplexAnnouncement() {
		return attachmentTypeComplexAnnouncement;
	}

	public void setAttachmentTypeComplexAnnouncement(AttachmentType attachmentTypeComplexAnnouncement) {
		this.attachmentTypeComplexAnnouncement = attachmentTypeComplexAnnouncement;
	}

	public CustomerOrderOrigin getCustomerOrderOriginWebSite() {
		return customerOrderOriginWebSite;
	}

	public void setCustomerOrderOriginWebSite(CustomerOrderOrigin customerOrderOriginWebSite) {
		this.customerOrderOriginWebSite = customerOrderOriginWebSite;
	}

	public CustomerOrderOrigin getCustomerOrderOriginOsiris() {
		return customerOrderOriginOsiris;
	}

	public void setCustomerOrderOriginOsiris(CustomerOrderOrigin customerOrderOriginOsiris) {
		this.customerOrderOriginOsiris = customerOrderOriginOsiris;
	}

	public AttachmentType getAttachmentTypeProviderInvoice() {
		return attachmentTypeProviderInvoice;
	}

	public void setAttachmentTypeProviderInvoice(AttachmentType attachmentTypeProviderInvoice) {
		this.attachmentTypeProviderInvoice = attachmentTypeProviderInvoice;
	}

	public Vat getVatTwo() {
		return vatTwo;
	}

	public void setVatTwo(Vat vatTwo) {
		this.vatTwo = vatTwo;
	}

	public Vat getVatDeductibleTwo() {
		return vatDeductibleTwo;
	}

	public void setVatDeductibleTwo(Vat vatDeductibleTwo) {
		this.vatDeductibleTwo = vatDeductibleTwo;
	}

	public PaymentDeadlineType getPaymentDeadLineType30() {
		return paymentDeadLineType30;
	}

	public void setPaymentDeadLineType30(PaymentDeadlineType paymentDeadLineType30) {
		this.paymentDeadLineType30 = paymentDeadLineType30;
	}

	public Provider getProviderCentralPay() {
		return providerCentralPay;
	}

	public void setProviderCentralPay(Provider providerCentralPay) {
		this.providerCentralPay = providerCentralPay;
	}

	public CompetentAuthorityType getCompetentAuthorityTypeChambreAgriculture() {
		return competentAuthorityTypeChambreAgriculture;
	}

	public void setCompetentAuthorityTypeChambreAgriculture(
			CompetentAuthorityType competentAuthorityTypeChambreAgriculture) {
		this.competentAuthorityTypeChambreAgriculture = competentAuthorityTypeChambreAgriculture;
	}

	public CompetentAuthorityType getCompetentAuthorityTypeUrssaf() {
		return competentAuthorityTypeUrssaf;
	}

	public void setCompetentAuthorityTypeUrssaf(CompetentAuthorityType competentAuthorityTypeUrssaf) {
		this.competentAuthorityTypeUrssaf = competentAuthorityTypeUrssaf;
	}

	public BillingType getBillingTypeRneUpdate() {
		return billingTypeRneUpdate;
	}

	public void setBillingTypeRneUpdate(BillingType billingTypeRneUpdate) {
		this.billingTypeRneUpdate = billingTypeRneUpdate;
	}

	public TiersFollowupType getTiersFollowupTypeInvoiceReminder() {
		return tiersFollowupTypeInvoiceReminder;
	}

	public void setTiersFollowupTypeInvoiceReminder(TiersFollowupType tiersFollowupTypeInvoiceReminder) {
		this.tiersFollowupTypeInvoiceReminder = tiersFollowupTypeInvoiceReminder;
	}

	public AccountingAccount getAccountingAccountLost() {
		return accountingAccountLost;
	}

	public void setAccountingAccountLost(AccountingAccount accountingAccountLost) {
		this.accountingAccountLost = accountingAccountLost;
	}

	public AccountingAccount getAccountingAccountProfit() {
		return accountingAccountProfit;
	}

	public void setAccountingAccountProfit(AccountingAccount accountingAccountProfit) {
		this.accountingAccountProfit = accountingAccountProfit;
	}

	public TiersCategory getTiersCategoryPresse() {
		return tiersCategoryPresse;
	}

	public void setTiersCategoryPresse(TiersCategory tiersCategoryPresse) {
		this.tiersCategoryPresse = tiersCategoryPresse;
	}

	public RffFrequency getRffFrequencyAnnual() {
		return rffFrequencyAnnual;
	}

	public void setRffFrequencyAnnual(RffFrequency rffFrequencyAnnual) {
		this.rffFrequencyAnnual = rffFrequencyAnnual;
	}

	public RffFrequency getRffFrequencyQuarterly() {
		return rffFrequencyQuarterly;
	}

	public void setRffFrequencyQuarterly(RffFrequency rffFrequencyQuarterly) {
		this.rffFrequencyQuarterly = rffFrequencyQuarterly;
	}

	public RffFrequency getRffFrequencyMonthly() {
		return rffFrequencyMonthly;
	}

	public void setRffFrequencyMonthly(RffFrequency rffFrequencyMonthly) {
		this.rffFrequencyMonthly = rffFrequencyMonthly;
	}

	public CustomerOrderFrequency getCustomerOrderFrequencyAnnual() {
		return customerOrderFrequencyAnnual;
	}

	public void setCustomerOrderFrequencyAnnual(CustomerOrderFrequency customerOrderFrequencyAnnual) {
		this.customerOrderFrequencyAnnual = customerOrderFrequencyAnnual;
	}

	public CustomerOrderFrequency getCustomerOrderFrequencyQuarterly() {
		return customerOrderFrequencyQuarterly;
	}

	public void setCustomerOrderFrequencyQuarterly(CustomerOrderFrequency customerOrderFrequencyQuarterly) {
		this.customerOrderFrequencyQuarterly = customerOrderFrequencyQuarterly;
	}

	public CustomerOrderFrequency getCustomerOrderFrequencyMonthly() {
		return customerOrderFrequencyMonthly;
	}

	public void setCustomerOrderFrequencyMonthly(CustomerOrderFrequency customerOrderFrequencyMonthly) {
		this.customerOrderFrequencyMonthly = customerOrderFrequencyMonthly;
	}

	public BillingType getBillingTypeRff() {
		return billingTypeRff;
	}

	public void setBillingTypeRff(BillingType billingTypeRff) {
		this.billingTypeRff = billingTypeRff;
	}

	public CompetentAuthorityType getCompetentAuthorityTypeSpfe() {
		return competentAuthorityTypeSpfe;
	}

	public void setCompetentAuthorityTypeSpfe(CompetentAuthorityType competentAuthorityTypeSpfe) {
		this.competentAuthorityTypeSpfe = competentAuthorityTypeSpfe;
	}

	public CompetentAuthorityType getCompetentAuthorityTypeInsee() {
		return competentAuthorityTypeInsee;
	}

	public void setCompetentAuthorityTypeInsee(CompetentAuthorityType competentAuthorityTypeInsee) {
		this.competentAuthorityTypeInsee = competentAuthorityTypeInsee;
	}

	public ServiceType getServiceTypeOther() {
		return serviceTypeOther;
	}

	public void setServiceTypeOther(ServiceType serviceTypeOther) {
		this.serviceTypeOther = serviceTypeOther;
	}

	public ServiceType getServiceTypeAnnualSubscription() {
		return serviceTypeAnnualSubscription;
	}

	public void setServiceTypeAnnualSubscription(ServiceType serviceTypeAnnualSubscription) {
		this.serviceTypeAnnualSubscription = serviceTypeAnnualSubscription;
	}

	public ServiceType getServiceTypeEnterpriseAnnualSubscription() {
		return serviceTypeEnterpriseAnnualSubscription;
	}

	public void setServiceTypeEnterpriseAnnualSubscription(ServiceType serviceTypeEnterpriseAnnualSubscription) {
		this.serviceTypeEnterpriseAnnualSubscription = serviceTypeEnterpriseAnnualSubscription;
	}

	public ServiceType getServiceTypeMonthlySubscription() {
		return serviceTypeMonthlySubscription;
	}

	public void setServiceTypeMonthlySubscription(ServiceType serviceTypeMonthlySubscription) {
		this.serviceTypeMonthlySubscription = serviceTypeMonthlySubscription;
	}

	public ServiceType getServiceTypeKioskNewspaperBuy() {
		return serviceTypeKioskNewspaperBuy;
	}

	public void setServiceTypeKioskNewspaperBuy(ServiceType serviceTypeKioskNewspaperBuy) {
		this.serviceTypeKioskNewspaperBuy = serviceTypeKioskNewspaperBuy;
	}

	public ServiceType getServiceTypeUniqueArticleBuy() {
		return serviceTypeUniqueArticleBuy;
	}

	public void setServiceTypeUniqueArticleBuy(ServiceType serviceTypeUniqueArticleBuy) {
		this.serviceTypeUniqueArticleBuy = serviceTypeUniqueArticleBuy;
	}

	public SpecialOffer getSpecialOfferJssSubscriptionReduction() {
		return specialOfferJssSubscriptionReduction;
	}

	public void setSpecialOfferJssSubscriptionReduction(SpecialOffer specialOfferJssSubscriptionReduction) {
		this.specialOfferJssSubscriptionReduction = specialOfferJssSubscriptionReduction;
	}

	public ProvisionType getProvisionTypeBilanPublication() {
		return provisionTypeBilanPublication;
	}

	public void setProvisionTypeBilanPublication(ProvisionType provisionTypeBilanPublication) {
		this.provisionTypeBilanPublication = provisionTypeBilanPublication;
	}

	public AccountingJournal getAccountingJournalBilan() {
		return accountingJournalBilan;
	}

	public void setAccountingJournalBilan(AccountingJournal accountingJournalBilan) {
		this.accountingJournalBilan = accountingJournalBilan;
	}

	public AttachmentType getAttachmentTypeContract() {
		return attachmentTypeContract;
	}

	public void setAttachmentTypeContract(AttachmentType attachmentTypeContract) {
		this.attachmentTypeContract = attachmentTypeContract;
	}

	public AttachmentType getAttachmentTypeTemplate() {
		return attachmentTypeTemplate;
	}

	public void setAttachmentTypeTemplate(AttachmentType attachmentTypeTemplate) {
		this.attachmentTypeTemplate = attachmentTypeTemplate;
	}

	public BillingType getBillingTypeDomiciliationContractTypeKeepMail() {
		return billingTypeDomiciliationContractTypeKeepMail;
	}

	public void setBillingTypeDomiciliationContractTypeKeepMail(
			BillingType billingTypeDomiciliationContractTypeKeepMail) {
		this.billingTypeDomiciliationContractTypeKeepMail = billingTypeDomiciliationContractTypeKeepMail;
	}

	public BillingType getBillingTypeDomiciliationContractTypeRouteEmail() {
		return billingTypeDomiciliationContractTypeRouteEmail;
	}

	public void setBillingTypeDomiciliationContractTypeRouteEmail(
			BillingType billingTypeDomiciliationContractTypeRouteEmail) {
		this.billingTypeDomiciliationContractTypeRouteEmail = billingTypeDomiciliationContractTypeRouteEmail;
	}

	public BillingType getBillingTypeDomiciliationContractTypeRouteMail() {
		return billingTypeDomiciliationContractTypeRouteMail;
	}

	public void setBillingTypeDomiciliationContractTypeRouteMail(
			BillingType billingTypeDomiciliationContractTypeRouteMail) {
		this.billingTypeDomiciliationContractTypeRouteMail = billingTypeDomiciliationContractTypeRouteMail;
	}

	public BillingType getBillingTypeDomiciliationContractTypeRouteEmailAndMail() {
		return billingTypeDomiciliationContractTypeRouteEmailAndMail;
	}

	public void setBillingTypeDomiciliationContractTypeRouteEmailAndMail(
			BillingType billingTypeDomiciliationContractTypeRouteEmailAndMail) {
		this.billingTypeDomiciliationContractTypeRouteEmailAndMail = billingTypeDomiciliationContractTypeRouteEmailAndMail;
	}

	public MailRedirectionType getMailRedirectionTypeLegalGuardian() {
		return mailRedirectionTypeLegalGuardian;
	}

	public void setMailRedirectionTypeLegalGuardian(MailRedirectionType mailRedirectionTypeLegalGuardian) {
		this.mailRedirectionTypeLegalGuardian = mailRedirectionTypeLegalGuardian;
	}

	public MailRedirectionType getMailRedirectionTypeActivity() {
		return mailRedirectionTypeActivity;
	}

	public void setMailRedirectionTypeActivity(MailRedirectionType mailRedirectionTypeActivity) {
		this.mailRedirectionTypeActivity = mailRedirectionTypeActivity;
	}

	public LocalDate getDateAccountingClosureForAccountant() {
		return dateAccountingClosureForAccountant;
	}

	public void setDateAccountingClosureForAccountant(LocalDate dateAccountingClosureForAccountant) {
		this.dateAccountingClosureForAccountant = dateAccountingClosureForAccountant;
	}

	public LocalDate getDateAccountingClosureForAll() {
		return dateAccountingClosureForAll;
	}

	public void setDateAccountingClosureForAll(LocalDate dateAccountingClosureForAll) {
		this.dateAccountingClosureForAll = dateAccountingClosureForAll;
	}

	public AttachmentType getAttachmentTypeRefusInfogreffe() {
		return attachmentTypeRefusInfogreffe;
	}

	public void setAttachmentTypeRefusInfogreffe(AttachmentType attachmentTypeRefusInfogreffe) {
		this.attachmentTypeRefusInfogreffe = attachmentTypeRefusInfogreffe;
	}

	public AttachmentType getAttachmentTypeAutreInfogreffe() {
		return attachmentTypeAutreInfogreffe;
	}

	public void setAttachmentTypeAutreInfogreffe(AttachmentType attachmentTypeAutreInfogreffe) {
		this.attachmentTypeAutreInfogreffe = attachmentTypeAutreInfogreffe;
	}

	public ActiveDirectoryGroup getActiveDirectoryGroupFormalites() {
		return activeDirectoryGroupFormalites;
	}

	public void setActiveDirectoryGroupFormalites(ActiveDirectoryGroup activeDirectoryGroupFormalites) {
		this.activeDirectoryGroupFormalites = activeDirectoryGroupFormalites;
	}

	public ActiveDirectoryGroup getActiveDirectoryGroupFacturation() {
		return activeDirectoryGroupFacturation;
	}

	public void setActiveDirectoryGroupFacturation(ActiveDirectoryGroup activeDirectoryGroupFacturation) {
		this.activeDirectoryGroupFacturation = activeDirectoryGroupFacturation;
	}

	public ProvisionType getProvisionTypeRegistrationAct() {
		return provisionTypeRegistrationAct;
	}

	public void setProvisionTypeRegistrationAct(ProvisionType provisionTypeRegistrationAct) {
		this.provisionTypeRegistrationAct = provisionTypeRegistrationAct;
	}

	public ServiceFieldType getFurtherInformationServiceFieldType() {
		return furtherInformationServiceFieldType;
	}

	public void setFurtherInformationServiceFieldType(ServiceFieldType furtherInformationServiceFieldType) {
		this.furtherInformationServiceFieldType = furtherInformationServiceFieldType;
	}

	public ActiveDirectoryGroup getActiveDirectoryGroupSales() {
		return activeDirectoryGroupSales;
	}

	public void setActiveDirectoryGroupSales(ActiveDirectoryGroup activeDirectoryGroupSales) {
		this.activeDirectoryGroupSales = activeDirectoryGroupSales;
	}

	public Responsable getResponsableDummyCustomerFrance() {
		return responsableDummyCustomerFrance;
	}

	public void setResponsableDummyCustomerFrance(Responsable responsableDummyCustomerFrance) {
		this.responsableDummyCustomerFrance = responsableDummyCustomerFrance;
	}

	public ProvisionScreenType getProvisionScreenTypeAnnouncement() {
		return provisionScreenTypeAnnouncement;
	}

	public void setProvisionScreenTypeAnnouncement(ProvisionScreenType provisionScreenTypeAnnouncement) {
		this.provisionScreenTypeAnnouncement = provisionScreenTypeAnnouncement;
	}

	public CustomerOrderOrigin getCustomerOrderOriginMyJss() {
		return customerOrderOriginMyJss;
	}

	public void setCustomerOrderOriginMyJss(CustomerOrderOrigin customerOrderOriginMyJss) {
		this.customerOrderOriginMyJss = customerOrderOriginMyJss;
	}

	public Category getCategoryInterview() {
		return categoryInterview;
	}

	public void setCategoryInterview(Category categoryInterview) {
		this.categoryInterview = categoryInterview;
	}

	public Category getCategoryPodcast() {
		return categoryPodcast;
	}

	public void setCategoryPodcast(Category categoryPodcast) {
		this.categoryPodcast = categoryPodcast;
	}

	public Category getCategoryArticle() {
		return categoryArticle;
	}

	public void setCategoryArticle(Category categoryArticle) {
		this.categoryArticle = categoryArticle;
	}

	public Category getCategorySerie() {
		return categorySerie;
	}

	public void setCategorySerie(Category categorySerie) {
		this.categorySerie = categorySerie;
	}

	public AttachmentType getAttachmentTypeClientCommunication() {
		return attachmentTypeClientCommunication;
	}

	public void setAttachmentTypeClientCommunication(AttachmentType attachmentTypeClientCommunication) {
		this.attachmentTypeClientCommunication = attachmentTypeClientCommunication;
	}

	public TypeDocument getDocumentTypeSynthesisRbeSigned() {
		return documentTypeSynthesisRbeSigned;
	}

	public void setDocumentTypeSynthesisRbeSigned(TypeDocument documentTypeSynthesisRbeSigned) {
		this.documentTypeSynthesisRbeSigned = documentTypeSynthesisRbeSigned;
	}

	public TypeDocument getDocumentTypeSynthesisRbeUnsigned() {
		return documentTypeSynthesisRbeUnsigned;
	}

	public void setDocumentTypeSynthesisRbeUnsigned(TypeDocument documentTypeSynthesisRbeUnsigned) {
		this.documentTypeSynthesisRbeUnsigned = documentTypeSynthesisRbeUnsigned;
	}

	public ProvisionType getProvisionTypeRbe() {
		return provisionTypeRbe;
	}

	public ProvisionFamilyType getProvisionFamilyTypeDeposit() {
		return provisionFamilyTypeDeposit;
	}

	public void setProvisionFamilyTypeDeposit(ProvisionFamilyType provisionFamilyTypeDeposit) {
		this.provisionFamilyTypeDeposit = provisionFamilyTypeDeposit;
	}

	public ProvisionFamilyType getProvisionFamilyTypeAbonnement() {
		return provisionFamilyTypeAbonnement;
	}

	public void setProvisionFamilyTypeAbonnement(ProvisionFamilyType provisionFamilyTypeAbonnement) {
		this.provisionFamilyTypeAbonnement = provisionFamilyTypeAbonnement;
	}

	public AccountingJournal getAccountingJournalSalary() {
		return accountingJournalSalary;
	}

	public void setAccountingJournalSalary(AccountingJournal accountingJournalSalary) {
		this.accountingJournalSalary = accountingJournalSalary;
	}

	public String getRecoverySharedMaiblox() {
		return recoverySharedMaiblox;
	}

	public void setRecoverySharedMaiblox(String recoverySharedMaiblox) {
		this.recoverySharedMaiblox = recoverySharedMaiblox;
	}

	public void setProvisionTypeRbe(ProvisionType provisionTypeRbe) {
		this.provisionTypeRbe = provisionTypeRbe;
	}

	public AccountingAccountClass getAccountingAccountClassProduct() {
		return accountingAccountClassProduct;
	}

	public void setAccountingAccountClassProduct(AccountingAccountClass accountingAccountClassProduct) {
		this.accountingAccountClassProduct = accountingAccountClassProduct;
	}

	public TypeDocument getDocumentTypeSynthesisUnsigned() {
		return documentTypeSynthesisUnsigned;
	}

	public void setDocumentTypeSynthesisUnsigned(TypeDocument documentTypeSynthesisUnsigned) {
		this.documentTypeSynthesisUnsigned = documentTypeSynthesisUnsigned;
	}

	public ServiceFamily getServiceFamilyImmatriculationAlAndFormality() {
		return serviceFamilyImmatriculationAlAndFormality;
	}

	public void setServiceFamilyImmatriculationAlAndFormality(
			ServiceFamily serviceFamilyImmatriculationAlAndFormality) {
		this.serviceFamilyImmatriculationAlAndFormality = serviceFamilyImmatriculationAlAndFormality;
	}

	public MyJssCategory getMyJssCategoryAnnouncement() {
		return myJssCategoryAnnouncement;
	}

	public void setMyJssCategoryAnnouncement(MyJssCategory announcementMyJssCategory) {
		this.myJssCategoryAnnouncement = announcementMyJssCategory;
	}

	public MyJssCategory getMyJssCategoryFormality() {
		return myJssCategoryFormality;
	}

	public void setMyJssCategoryFormality(MyJssCategory formalityMyJssCategory) {
		this.myJssCategoryFormality = formalityMyJssCategory;
	}

	public JssCategory getJssCategoryHomepageFirstHighlighted() {
		return jssCategoryHomepageFirstHighlighted;
	}

	public void setJssCategoryHomepageFirstHighlighted(JssCategory jssCategoryHomepageFirstHighlighted) {
		this.jssCategoryHomepageFirstHighlighted = jssCategoryHomepageFirstHighlighted;
	}

	public JssCategory getJssCategoryHomepageSecondHighlighted() {
		return jssCategoryHomepageSecondHighlighted;
	}

	public void setJssCategoryHomepageSecondHighlighted(JssCategory jssCategoryHomepageSecondHighlighted) {
		this.jssCategoryHomepageSecondHighlighted = jssCategoryHomepageSecondHighlighted;
	}

	public JssCategory getJssCategoryHomepageThirdHighlighted() {
		return jssCategoryHomepageThirdHighlighted;
	}

	public void setJssCategoryHomepageThirdHighlighted(JssCategory jssCategoryHomepageThirdHighlighted) {
		this.jssCategoryHomepageThirdHighlighted = jssCategoryHomepageThirdHighlighted;
	}

	public ServiceType getServiceTypeSecondaryCenterOpeningAlAndFormality() {
		return serviceTypeSecondaryCenterOpeningAlAndFormality;
	}

	public void setServiceTypeSecondaryCenterOpeningAlAndFormality(
			ServiceType serviceTypeSecondaryCenterOpeningAlAndFormality) {
		this.serviceTypeSecondaryCenterOpeningAlAndFormality = serviceTypeSecondaryCenterOpeningAlAndFormality;
	}

	public ServiceFamilyGroup getServiceFamilyGroupAnnouncement() {
		return serviceFamilyGroupAnnouncement;
	}

	public void setServiceFamilyGroupAnnouncement(ServiceFamilyGroup serviceFamilyGroupAnnouncement) {
		this.serviceFamilyGroupAnnouncement = serviceFamilyGroupAnnouncement;
	}

	public ProvisionType getProvisionTypeCharacterAnnouncement() {
		return provisionTypeCharacterAnnouncement;
	}

	public void setProvisionTypeCharacterAnnouncement(ProvisionType provisionTypeCharacterAnnouncement) {
		this.provisionTypeCharacterAnnouncement = provisionTypeCharacterAnnouncement;
	}

	public Category getCategoryExclusivity() {
		return categoryExclusivity;
	}

	public void setCategoryExclusivity(Category categoryExclusivity) {
		this.categoryExclusivity = categoryExclusivity;
	}

	public ActiveDirectoryGroup getActiveDirectoryGroupInsertions() {
		return activeDirectoryGroupInsertions;
	}

	public void setActiveDirectoryGroupInsertions(ActiveDirectoryGroup activeDirectoryGroupInsertions) {
		this.activeDirectoryGroupInsertions = activeDirectoryGroupInsertions;
	}

	public String getStringMyJssDemoRequestMail() {
		return stringMyJssDemoRequestMail;
	}

	public void setStringMyJssDemoRequestMail(String stringMyJssDemoRequestMail) {
		this.stringMyJssDemoRequestMail = stringMyJssDemoRequestMail;
	}

	public String getStringMyJssWebinarRequestMail() {
		return stringMyJssWebinarRequestMail;
	}

	public void setStringMyJssWebinarRequestMail(String stringMyJssWebinarRequestMail) {
		this.stringMyJssWebinarRequestMail = stringMyJssWebinarRequestMail;
	}

	public String getStringMyJssContactFormRequestMail() {
		return stringMyJssContactFormRequestMail;
	}

	public void setStringMyJssContactFormRequestMail(String stringMyJssContactFormRequestMail) {
		this.stringMyJssContactFormRequestMail = stringMyJssContactFormRequestMail;
	}

	public AccountingAccountClass getAccountingAccountClassTiers() {
		return accountingAccountClassTiers;
	}

	public void setAccountingAccountClassTiers(AccountingAccountClass accountingAccountClassTiers) {
		this.accountingAccountClassTiers = accountingAccountClassTiers;
	}

	public AttachmentType getAttachmentTypeApplicationCv() {
		return attachmentTypeApplicationCv;
	}

	public void setAttachmentTypeApplicationCv(AttachmentType attachmentTypeApplicationCv) {
		this.attachmentTypeApplicationCv = attachmentTypeApplicationCv;
	}

	public Employee getEmployeeCandidacyResponsible() {
		return employeeCandidacyResponsible;
	}

	public void setEmployeeCandidacyResponsible(Employee employeeCandidacyResponsible) {
		this.employeeCandidacyResponsible = employeeCandidacyResponsible;
	}

	public MyJssCategory getMyJssCategoryApostille() {
		return myJssCategoryApostille;
	}

	public void setMyJssCategoryApostille(MyJssCategory myJssCategoryApostille) {
		this.myJssCategoryApostille = myJssCategoryApostille;
	}

	public MyJssCategory getMyJssCategoryDocument() {
		return myJssCategoryDocument;
	}

	public void setMyJssCategoryDocument(MyJssCategory myJssCategoryDocument) {
		this.myJssCategoryDocument = myJssCategoryDocument;
	}

	public MyJssCategory getMyJssCategoryDomiciliation() {
		return myJssCategoryDomiciliation;
	}

	public void setMyJssCategoryDomiciliation(MyJssCategory myJssCategoryDomiciliation) {
		this.myJssCategoryDomiciliation = myJssCategoryDomiciliation;
	}

	public PublishingDepartment getPublishingDepartmentIdf() {
		return publishingDepartmentIdf;
	}

	public void setPublishingDepartmentIdf(PublishingDepartment publishingDepartmentIdf) {
		this.publishingDepartmentIdf = publishingDepartmentIdf;
	}

	public Employee getEmployeeProductionDirector() {
		return employeeProductionDirector;
	}

	public void setEmployeeProductionDirector(Employee employeeProductionDirector) {
		this.employeeProductionDirector = employeeProductionDirector;
	}

	public AccountingJournal getAccountingJournalSituation() {
		return accountingJournalSituation;
	}

	public void setAccountingJournalSituation(AccountingJournal accountingJournalSituation) {
		this.accountingJournalSituation = accountingJournalSituation;
	}

	public ServiceFamilyGroup getServiceFamilyGroupOther() {
		return serviceFamilyGroupOther;
	}

	public void setServiceFamilyGroupOther(ServiceFamilyGroup serviceFamilyGroupOther) {
		this.serviceFamilyGroupOther = serviceFamilyGroupOther;
	}

	public ServiceFamilyGroup getServiceFamilyGroupFormality() {
		return serviceFamilyGroupFormality;
	}

	public void setServiceFamilyGroupFormality(ServiceFamilyGroup serviceFamilyGroupFormality) {
		this.serviceFamilyGroupFormality = serviceFamilyGroupFormality;
	}

	public AssignationType getAssignationTypeFormaliste() {
		return assignationTypeFormaliste;
	}

	public void setAssignationTypeFormaliste(AssignationType assignationTypeFormaliste) {
		this.assignationTypeFormaliste = assignationTypeFormaliste;
	}

	public AssignationType getAssignationTypePublisciste() {
		return assignationTypePublisciste;
	}

	public void setAssignationTypePublisciste(AssignationType assignationTypePublisciste) {
		this.assignationTypePublisciste = assignationTypePublisciste;
	}

	public ProvisionFamilyType getProvisionFamilyTypeBodacc() {
		return provisionFamilyTypeBodacc;
	}

	public void setProvisionFamilyTypeBodacc(ProvisionFamilyType provisionFamilyTypeBodacc) {
		this.provisionFamilyTypeBodacc = provisionFamilyTypeBodacc;
	}

	public ProvisionFamilyType getProvisionFamilyTypeBalo() {
		return provisionFamilyTypeBalo;
	}

	public void setProvisionFamilyTypeBalo(ProvisionFamilyType provisionFamilyTypeBalo) {
		this.provisionFamilyTypeBalo = provisionFamilyTypeBalo;
	}

	public ActiveDirectoryGroup getActiveDirectoryGroupDirection() {
		return activeDirectoryGroupDirection;
	}

	public void setActiveDirectoryGroupDirection(ActiveDirectoryGroup activeDirectoryGroupDirection) {
		this.activeDirectoryGroupDirection = activeDirectoryGroupDirection;
	}

	public Affaire getAffaireDummyForSubscription() {
		return affaireDummyForSubscription;
	}

	public void setAffaireDummyForSubscription(Affaire affaireDummyForSubscription) {
		this.affaireDummyForSubscription = affaireDummyForSubscription;
	}

	public BillingType getBillingTypeRedactedByJssSimple() {
		return billingTypeRedactedByJssSimple;
	}

	public void setBillingTypeRedactedByJssSimple(BillingType billingTypeRedactedByJssSimple) {
		this.billingTypeRedactedByJssSimple = billingTypeRedactedByJssSimple;
	}

	public BillingType getBillingTypeRedactedByJssComplex() {
		return billingTypeRedactedByJssComplex;
	}

	public void setBillingTypeRedactedByJssComplex(BillingType billingTypeRedactedByJssComplex) {
		this.billingTypeRedactedByJssComplex = billingTypeRedactedByJssComplex;
	}

	public BillingType getBillingTypeRedactedByJssFree() {
		return billingTypeRedactedByJssFree;
	}

	public void setBillingTypeRedactedByJssFree(BillingType billingTypeRedactedByJssFree) {
		this.billingTypeRedactedByJssFree = billingTypeRedactedByJssFree;
	}

	public BillingType getBillingTypeKbis() {
		return billingTypeKbis;
	}

	public void setBillingTypeKbis(BillingType billingTypeKbis) {
		this.billingTypeKbis = billingTypeKbis;
	}

	public AttachmentType getAttachmentTypePurchaseOrder() {
		return attachmentTypePurchaseOrder;
	}

	public void setAttachmentTypePurchaseOrder(AttachmentType attachmentTypePurchaseOrder) {
		this.attachmentTypePurchaseOrder = attachmentTypePurchaseOrder;
	}

}
