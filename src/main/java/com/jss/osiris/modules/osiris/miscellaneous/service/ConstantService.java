package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.time.LocalDate;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.wordpress.model.Category;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccount;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.osiris.accounting.model.AccountingJournal;
import com.jss.osiris.modules.osiris.accounting.model.PrincipalAccountingAccount;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.osiris.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.osiris.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingType;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthorityType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Constant;
import com.jss.osiris.modules.osiris.miscellaneous.model.Country;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.osiris.miscellaneous.model.DeliveryService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Department;
import com.jss.osiris.modules.osiris.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Language;
import com.jss.osiris.modules.osiris.miscellaneous.model.LegalForm;
import com.jss.osiris.modules.osiris.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Provider;
import com.jss.osiris.modules.osiris.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.osiris.miscellaneous.model.Vat;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.ActType;
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
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeFormalite;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypePersonne;
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

public interface ConstantService {
    public Constant getConstants() throws OsirisException;

    public Constant addOrUpdateConstant(Constant constant) throws OsirisException;

    public BillingLabelType getBillingLabelTypeCodeAffaire() throws OsirisException;

    public BillingLabelType getBillingLabelTypeOther() throws OsirisException;

    public BillingLabelType getBillingLabelTypeCustomer() throws OsirisException;

    public AccountingJournal getAccountingJournalSales() throws OsirisException;

    public AccountingJournal getAccountingJournalSalary() throws OsirisException;

    public AccountingJournal getAccountingJournalPurchases() throws OsirisException;

    public AccountingJournal getAccountingJournalANouveau() throws OsirisException;

    public AccountingJournal getAccountingJournalBank() throws OsirisException;

    public AccountingJournal getAccountingJournalCash() throws OsirisException;

    public AccountingJournal getAccountingJournalBilan() throws OsirisException;

    public AccountingJournal getAccountingJournalMiscellaneousOperations() throws OsirisException;

    public TiersType getTiersTypeProspect() throws OsirisException;

    public TiersType getTiersTypeClient() throws OsirisException;

    public DocumentType getDocumentTypeDigital() throws OsirisException;

    public DocumentType getDocumentTypePaper() throws OsirisException;

    public DocumentType getDocumentTypeBilling() throws OsirisException;

    public DocumentType getDocumentTypeDunning() throws OsirisException;

    public DocumentType getDocumentTypeRefund() throws OsirisException;

    public TypeDocument getDocumentTypeSynthesisRbeSigned() throws OsirisException;

    public TypeDocument getDocumentTypeSynthesisRbeUnsigned() throws OsirisException;

    public TypeDocument getDocumentTypeSynthesisUnsigned() throws OsirisException;

    public DocumentType getDocumentTypeBillingClosure() throws OsirisException;

    public DocumentType getDocumentTypeProvisionnalReceipt() throws OsirisException;

    public AttachmentType getAttachmentTypeKbis() throws OsirisException;

    public AttachmentType getAttachmentTypeCni() throws OsirisException;

    public AttachmentType getAttachmentTypeLogo() throws OsirisException;

    public AttachmentType getAttachmentTypeProofOfAddress() throws OsirisException;

    public AttachmentType getAttachmentTypeInvoice() throws OsirisException;

    public AttachmentType getAttachmentTypeProviderInvoice() throws OsirisException;

    public AttachmentType getAttachmentTypeCreditNote() throws OsirisException;

    public AttachmentType getAttachmentTypeKbisUpdated() throws OsirisException;

    public AttachmentType getAttachmentTypeRbe() throws OsirisException;

    public AttachmentType getAttachmentTypeDepositReceipt() throws OsirisException;

    public AttachmentType getAttachmentTypePublicationFlag() throws OsirisException;

    public AttachmentType getAttachmentTypePublicationReceipt() throws OsirisException;

    public AttachmentType getAttachmentTypePublicationProof() throws OsirisException;

    public AttachmentType getAttachmentTypeJournal() throws OsirisException;

    public AttachmentType getAttachmentTypeBillingClosure() throws OsirisException;

    public AttachmentType getAttachmentTypeProofReading() throws OsirisException;

    public AttachmentType getAttachmentTypeAutomaticMail() throws OsirisException;

    public AttachmentType getAttachmentTypeAnnouncement() throws OsirisException;

    public AttachmentType getAttachmentTypeContract() throws OsirisException;

    public AttachmentType getAttachmentTypeComplexAnnouncement() throws OsirisException;

    public AttachmentType getAttachmentTypeTemplate() throws OsirisException;

    public AttachmentType getAttachmentTypeQuotation() throws OsirisException;

    public AttachmentType getAttachmentTypeRefusInfogreffe() throws OsirisException;

    public AttachmentType getAttachmentTypeAutreInfogreffe() throws OsirisException;

    public AttachmentType getAttachmentTypeClientCommunication() throws OsirisException;

    public AttachmentType getAttachmentTypeApplicationCv() throws OsirisException;

    public Country getCountryFrance() throws OsirisException;

    public Country getCountryMonaco() throws OsirisException;

    public BillingType getBillingTypeDeboursNonTaxable() throws OsirisException;

    public BillingType getBillingTypeCentralPayFees() throws OsirisException;

    public BillingType getBillingTypeRff() throws OsirisException;

    public BillingType getBillingTypeLogo() throws OsirisException;

    public BillingType getBillingTypeRedactedByJss() throws OsirisException;

    public BillingType getBillingTypeBaloPackage() throws OsirisException;

    public BillingType getBillingTypeBaloNormalization() throws OsirisException;

    public BillingType getBillingTypeBaloPublicationFlag() throws OsirisException;

    public BillingType getBillingTypePublicationPaper() throws OsirisException;

    public BillingType getBillingTypePublicationReceipt() throws OsirisException;

    public BillingType getBillingTypePublicationFlag() throws OsirisException;

    public BillingType getBillingTypeBodaccFollowup() throws OsirisException;

    public BillingType getBillingTypeBodaccFollowupAndRedaction() throws OsirisException;

    public BillingType getBillingTypeNantissementDeposit() throws OsirisException;

    public BillingType getBillingTypeSocialShareNantissementRedaction() throws OsirisException;

    public BillingType getBillingTypeBusinnessNantissementRedaction() throws OsirisException;

    public BillingType getBillingTypeSellerPrivilegeRedaction() throws OsirisException;

    public BillingType getBillingTypeTreatmentMultipleModiciation() throws OsirisException;

    public BillingType getBillingTypeVacationMultipleModification() throws OsirisException;

    public BillingType getBillingTypeRegisterPurchase() throws OsirisException;

    public BillingType getBillingTypeRegisterInitials() throws OsirisException;

    public BillingType getBillingTypeRegisterShippingCosts() throws OsirisException;

    public BillingType getBillingTypeDisbursement() throws OsirisException;

    public BillingType getBillingTypeFeasibilityStudy() throws OsirisException;

    public BillingType getBillingTypeChronopostFees() throws OsirisException;

    public BillingType getBillingTypeConfrereFees() throws OsirisException;

    public BillingType getBillingTypeShippingCosts() throws OsirisException;

    public BillingType getBillingTypeApplicationFees() throws OsirisException;

    public BillingType getBillingTypeBankCheque() throws OsirisException;

    public BillingType getBillingTypeComplexeFile() throws OsirisException;

    public BillingType getBillingTypeBilan() throws OsirisException;

    public ProvisionType getProvisionTypeBilanPublication() throws OsirisException;

    public ProvisionType getProvisionTypeRegistrationAct() throws OsirisException;

    public ProvisionType getProvisionTypeRbe() throws OsirisException;

    public ProvisionType getProvisionTypeCharacterAnnouncement() throws OsirisException;

    public ProvisionFamilyType getProvisionFamilyTypeDeposit() throws OsirisException;

    public BillingType getBillingTypeInfogreffeDebour() throws OsirisException;

    public BillingType getBillingTypeEmolumentsDeGreffeDebour() throws OsirisException;

    public BillingType getBillingTypeDocumentScanning() throws OsirisException;

    public BillingType getBillingTypeEmergency() throws OsirisException;

    public BillingType getBillingTypeRneUpdate() throws OsirisException;

    public BillingType getBillingtypeVacationUpdateBeneficialOwners() throws OsirisException;

    public BillingType getBillingtypeFormalityAdditionalDeclaration() throws OsirisException;

    public BillingType getBillingtypeCorrespondenceFees() throws OsirisException;

    public BillingType getBillingTypeDomiciliationContractTypeKeepMail() throws OsirisException;

    public BillingType getBillingTypeDomiciliationContractTypeRouteEmail() throws OsirisException;

    public BillingType getBillingTypeDomiciliationContractTypeRouteMail() throws OsirisException;

    public BillingType getBillingTypeDomiciliationContractTypeRouteEmailAndMail() throws OsirisException;

    public BillingType getBillingTypeSupplyFullBeCopy() throws OsirisException;

    public String getStringNantissementDepositFormeJuridiqueCode() throws OsirisException;

    public String getStrinSocialShareNantissementRedactionFormeJuridiqueCode() throws OsirisException;

    public String getStringBusinnessNantissementRedactionFormeJuridiqueCode() throws OsirisException;

    public PaymentType getPaymentTypeVirement() throws OsirisException;

    public PaymentType getPaymentTypeEspeces() throws OsirisException;

    public PaymentType getPaymentTypeCheques() throws OsirisException;

    public PaymentType getPaymentTypeCB() throws OsirisException;

    public PaymentType getPaymentTypePrelevement() throws OsirisException;

    public PaymentType getPaymentTypeAccount() throws OsirisException;

    public RefundType getRefundTypeVirement() throws OsirisException;

    public SubscriptionPeriodType getSubscriptionPeriodType12M() throws OsirisException;

    public LegalForm getLegalFormUnregistered() throws OsirisException;

    public JournalType getJournalTypeSpel() throws OsirisException;

    public JournalType getJournalTypePaper() throws OsirisException;

    public Confrere getConfrereJssSpel() throws OsirisException;

    public DomiciliationContractType getDomiciliationContractTypeKeepMail() throws OsirisException;

    public DomiciliationContractType getDomiciliationContractTypeRouteMail() throws OsirisException;

    public DomiciliationContractType getDomiciliationContractTypeRouteEmailAndMail() throws OsirisException;

    public DomiciliationContractType getDomiciliationContractTypeRouteEmail() throws OsirisException;

    public MailRedirectionType getMailRedirectionTypeOther() throws OsirisException;

    public MailRedirectionType getMailRedirectionTypeLegalGuardian() throws OsirisException;

    public MailRedirectionType getMailRedirectionTypeActivity() throws OsirisException;

    public ActType getActTypeSeing() throws OsirisException;

    public ActType getActTypeAuthentic() throws OsirisException;

    public AssignationType getAssignationTypeEmployee() throws OsirisException;

    public Employee getEmployeeBillingResponsible() throws OsirisException;

    public Employee getEmployeeMailResponsible() throws OsirisException;

    public Employee getEmployeeSalesDirector() throws OsirisException;

    public Employee getEmployeeInvoiceReminderResponsible() throws OsirisException;

    public Employee getEmployeeCandidacyResponsible() throws OsirisException;

    public TransfertFundsType getTransfertFundsTypePhysique() throws OsirisException;

    public TransfertFundsType getTransfertFundsTypeMoral() throws OsirisException;

    public TransfertFundsType getTransfertFundsTypeBail() throws OsirisException;

    public CompetentAuthority getCompetentAuthorityInfogreffe() throws OsirisException;

    public CompetentAuthority getCompetentAuthorityInpi() throws OsirisException;

    public CompetentAuthorityType getCompetentAuthorityTypeRcs() throws OsirisException;

    public CompetentAuthorityType getCompetentAuthorityTypeCfp() throws OsirisException;

    public CompetentAuthorityType getCompetentAuthorityTypeCci() throws OsirisException;

    public CompetentAuthorityType getCompetentAuthorityTypeChambreMetier() throws OsirisException;

    public CompetentAuthorityType getCompetentAuthorityTypeChambreAgriculture() throws OsirisException;

    public CompetentAuthorityType getCompetentAuthorityTypeUrssaf() throws OsirisException;

    public CompetentAuthorityType getCompetentAuthorityTypeDireccte() throws OsirisException;

    public CompetentAuthorityType getCompetentAuthorityTypePrefecture() throws OsirisException;

    public CompetentAuthorityType getCompetentAuthorityTypeSpfe() throws OsirisException;

    public CompetentAuthorityType getCompetentAuthorityTypeInsee() throws OsirisException;

    public InvoiceStatus getInvoiceStatusSend() throws OsirisException;

    public InvoiceStatus getInvoiceStatusReceived() throws OsirisException;

    public InvoiceStatus getInvoiceStatusPayed() throws OsirisException;

    public InvoiceStatus getInvoiceStatusCancelled() throws OsirisException;

    public InvoiceStatus getInvoiceStatusCreditNoteEmited() throws OsirisException;

    public InvoiceStatus getInvoiceStatusCreditNoteReceived() throws OsirisException;

    public Vat getVatTwenty() throws OsirisException;

    public Vat getVatZero() throws OsirisException;

    public Vat getVatTwo() throws OsirisException;

    public Vat getVatDeductibleTwo() throws OsirisException;

    public Vat getVatDeductible() throws OsirisException;

    public Department getDepartmentMartinique() throws OsirisException;

    public Department getDepartmentGuadeloupe() throws OsirisException;

    public Department getDepartmentReunion() throws OsirisException;

    public PublishingDepartment getPublishingDepartmentIdf() throws OsirisException;

    public TypePersonne getTypePersonnePersonnePhysique() throws OsirisException;

    public TypePersonne getTypePersonnePersonneMorale() throws OsirisException;

    public TypePersonne getTypePersonneExploitation() throws OsirisException;

    public TypeFormalite getTypeFormaliteCessation() throws OsirisException;

    public TypeFormalite getTypeFormaliteModification() throws OsirisException;

    public TypeFormalite getTypeFormaliteCreation() throws OsirisException;

    public TypeFormalite getTypeFormaliteCorrection() throws OsirisException;

    public String getStringAccountingSharedMaiblox() throws OsirisException;

    public String getStringSalesSharedMailbox() throws OsirisException;

    public String getRecoverySharedMaiblox() throws OsirisException;

    public BillingClosureRecipientType getBillingClosureRecipientTypeOther() throws OsirisException;

    public BillingClosureRecipientType getBillingClosureRecipientTypeClient() throws OsirisException;

    public BillingClosureRecipientType getBillingClosureRecipientTypeResponsable() throws OsirisException;

    public BillingClosureType getBillingClosureTypeAffaire() throws OsirisException;

    public DeliveryService getDeliveryServiceJss() throws OsirisException;

    public Language getLanguageFrench() throws OsirisException;

    public PrincipalAccountingAccount getPrincipalAccountingAccountProvider() throws OsirisException;

    public PrincipalAccountingAccount getPrincipalAccountingAccountDeposit() throws OsirisException;

    public PrincipalAccountingAccount getPrincipalAccountingAccountDepositProvider() throws OsirisException;

    public PrincipalAccountingAccount getPrincipalAccountingAccountLitigious() throws OsirisException;

    public PrincipalAccountingAccount getPrincipalAccountingAccountSuspicious() throws OsirisException;

    public PrincipalAccountingAccount getPrincipalAccountingAccountCustomer() throws OsirisException;

    public PrincipalAccountingAccount getPrincipalAccountingAccountProduct() throws OsirisException;

    public PrincipalAccountingAccount getPrincipalAccountingAccountCharge() throws OsirisException;

    public PrincipalAccountingAccount getPrincipalAccountingAccountBank() throws OsirisException;

    public PrincipalAccountingAccount getPrincipalAccountingAccountWaiting() throws OsirisException;

    public AccountingAccount getAccountingAccountLost() throws OsirisException;

    public AccountingAccount getAccountingAccountProfit() throws OsirisException;

    public AccountingAccount getAccountingAccountBankCentralPay() throws OsirisException;

    public AccountingAccount getAccountingAccountBankJss() throws OsirisException;

    public AccountingAccount getAccountingAccountCaisse() throws OsirisException;

    public CustomerOrderOrigin getCustomerOrderOriginOldWebSite() throws OsirisException;

    public CustomerOrderOrigin getCustomerOrderOriginMyJss() throws OsirisException;

    public CustomerOrderOrigin getCustomerOrderOriginOsiris() throws OsirisException;

    public PaymentDeadlineType getPaymentDeadLineType30() throws OsirisException;

    public Provider getProviderCentralPay() throws OsirisException;

    public TiersFollowupType getTiersFollowupTypeInvoiceReminder() throws OsirisException;

    public TiersCategory getTiersCategoryPresse() throws OsirisException;

    public RffFrequency getRffFrequencyAnnual() throws OsirisException;

    public RffFrequency getRffFrequencyMonthly() throws OsirisException;

    public RffFrequency getRffFrequencyQuarterly() throws OsirisException;

    public ServiceType getServiceTypeOther() throws OsirisException;

    public ServiceType getServiceTypeAnnualSubscription() throws OsirisException;

    public ServiceType getServiceTypeMonthlySubscription() throws OsirisException;

    public ServiceType getServiceTypeKioskNewspaperBuy() throws OsirisException;

    public ServiceType getServiceTypeUniqueArticleBuy() throws OsirisException;

    public SpecialOffer getSpecialOfferJssSubscriptionReduction() throws OsirisException;

    public ServiceType getServiceTypeSecondaryCenterOpeningAlAndFormality() throws OsirisException;

    public ActiveDirectoryGroup getActiveDirectoryGroupFormalites() throws OsirisException;

    public ActiveDirectoryGroup getActiveDirectoryGroupInsertions() throws OsirisException;

    public ActiveDirectoryGroup getActiveDirectoryGroupSales() throws OsirisException;

    public ActiveDirectoryGroup getActiveDirectoryGroupFacturation() throws OsirisException;

    public LocalDate getDateAccountingClosureForAll() throws OsirisException;

    public LocalDate getDateAccountingClosureForAccountant() throws OsirisException;

    public ServiceFieldType getFurtherInformationServiceFieldType() throws OsirisException;

    public Responsable getResponsableDummyCustomerFrance() throws OsirisException;

    public ProvisionScreenType getProvisionScreenTypeAnnouncement() throws OsirisException;

    public Category getCategoryInterview() throws OsirisException;

    public Category getCategoryPodcast() throws OsirisException;

    public Category getCategoryArticle() throws OsirisException;

    public Category getCategorySerie() throws OsirisException;

    public Category getCategoryExclusivity() throws OsirisException;

    public MyJssCategory getMyJssCategoryAnnouncement() throws OsirisException;

    public MyJssCategory getMyJssCategoryFormality() throws OsirisException;

    public MyJssCategory getMyJssCategoryDomiciliation() throws OsirisException;

    public MyJssCategory getMyJssCategoryApostille() throws OsirisException;

    public MyJssCategory getMyJssCategoryDocument() throws OsirisException;

    public JssCategory getJssCategoryHomepageFirstHighlighted() throws OsirisException;

    public JssCategory getJssCategoryHomepageSecondHighlighted() throws OsirisException;

    public JssCategory getJssCategoryHomepageThirdHighlighted() throws OsirisException;

    public AccountingAccountClass getAccountingAccountClassProduct() throws OsirisException;

    public AccountingAccountClass getAccountingAccountClassTiers() throws OsirisException;

    public ServiceFamily getServiceFamilyImmatriculationAlAndFormality() throws OsirisException;

    public ServiceFamilyGroup getServiceFamilyGroupAnnouncement() throws OsirisException;

    public String getStringMyJssDemoRequestMail() throws OsirisException;

    public String getStringMyJssWebinarRequestMail() throws OsirisException;

    public String getStringMyJssContactFormRequestMail() throws OsirisException;
}
