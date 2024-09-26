package com.jss.osiris.modules.miscellaneous.service;

import java.time.LocalDate;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.PrincipalAccountingAccount;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthorityType;
import com.jss.osiris.modules.miscellaneous.model.Constant;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.miscellaneous.model.DeliveryService;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.miscellaneous.model.Language;
import com.jss.osiris.modules.miscellaneous.model.LegalForm;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.miscellaneous.model.Provider;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.ActType;
import com.jss.osiris.modules.quotation.model.AssignationType;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.DomiciliationContractType;
import com.jss.osiris.modules.quotation.model.JournalType;
import com.jss.osiris.modules.quotation.model.MailRedirectionType;
import com.jss.osiris.modules.quotation.model.ProvisionType;
import com.jss.osiris.modules.quotation.model.ServiceFieldType;
import com.jss.osiris.modules.quotation.model.ServiceType;
import com.jss.osiris.modules.quotation.model.TransfertFundsType;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeFormalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypePersonne;
import com.jss.osiris.modules.tiers.model.BillingClosureRecipientType;
import com.jss.osiris.modules.tiers.model.BillingClosureType;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.PaymentDeadlineType;
import com.jss.osiris.modules.tiers.model.RefundType;
import com.jss.osiris.modules.tiers.model.RffFrequency;
import com.jss.osiris.modules.tiers.model.SubscriptionPeriodType;
import com.jss.osiris.modules.tiers.model.TiersCategory;
import com.jss.osiris.modules.tiers.model.TiersFollowupType;
import com.jss.osiris.modules.tiers.model.TiersType;

public interface ConstantService {
    public Constant getConstants() throws OsirisException;

    public Constant getConstant(Integer id) throws OsirisException;

    public Constant addOrUpdateConstant(Constant constant) throws OsirisException;

    public BillingLabelType getBillingLabelTypeCodeAffaire() throws OsirisException;

    public BillingLabelType getBillingLabelTypeOther() throws OsirisException;

    public BillingLabelType getBillingLabelTypeCustomer() throws OsirisException;

    public AccountingJournal getAccountingJournalSales() throws OsirisException;

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

    public TypePersonne getTypePersonnePersonnePhysique() throws OsirisException;

    public TypePersonne getTypePersonnePersonneMorale() throws OsirisException;

    public TypePersonne getTypePersonneExploitation() throws OsirisException;

    public TypeFormalite getTypeFormaliteCessation() throws OsirisException;

    public TypeFormalite getTypeFormaliteModification() throws OsirisException;

    public TypeFormalite getTypeFormaliteCreation() throws OsirisException;

    public TypeFormalite getTypeFormaliteCorrection() throws OsirisException;

    public String getStringAccountingSharedMaiblox() throws OsirisException;

    public String getStringSalesSharedMailbox() throws OsirisException;

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

    public CustomerOrderOrigin getCustomerOrderOriginWebSite() throws OsirisException;

    public CustomerOrderOrigin getCustomerOrderOriginOsiris() throws OsirisException;

    public PaymentDeadlineType getPaymentDeadLineType30() throws OsirisException;

    public Provider getProviderCentralPay() throws OsirisException;

    public TiersFollowupType getTiersFollowupTypeInvoiceReminder() throws OsirisException;

    public TiersCategory getTiersCategoryPresse() throws OsirisException;

    public RffFrequency getRffFrequencyAnnual() throws OsirisException;

    public RffFrequency getRffFrequencyMonthly() throws OsirisException;

    public RffFrequency getRffFrequencyQuarterly() throws OsirisException;

    public ServiceType getServiceTypeOther() throws OsirisException;

    public ActiveDirectoryGroup getActiveDirectoryGroupFormalites() throws OsirisException;

    public ActiveDirectoryGroup getActiveDirectoryGroupFacturation() throws OsirisException;

    public LocalDate getDateAccountingClosureForAll() throws OsirisException;

    public LocalDate getDateAccountingClosureForAccountant() throws OsirisException;

    public ServiceFieldType getFurtherInformationServiceFieldType() throws OsirisException;
}
