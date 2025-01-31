import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Constant } from '../../miscellaneous/model/Constant';

@Injectable({
  providedIn: 'root'
})
export class ConstantService extends AppRestService<Constant> {

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  constant: Constant = {} as Constant;

  getConstants() {
    return this.get(new HttpParams(), "constants");
  }

  addOrUpdateConstant(constant: Constant) {
    return this.addOrUpdate(new HttpParams(), "constant", constant, "Enregistré", "Erreur lors de l'enregistrement");
  }

  initConstant() {
    if (localStorage.getItem('constants') != null) {
      let a = localStorage.getItem('constants');
      this.constant = JSON.parse(a!) as Constant;
    }

    this.getConstants().subscribe(response => {
      this.constant = response;
      localStorage.setItem('constants', JSON.stringify(this.constant));
    });
  }

  getBillingLabelTypeCodeAffaire() {
    return this.constant.billingLabelTypeCodeAffaire;
  }

  getBillingLabelTypeOther() {
    return this.constant.billingLabelTypeOther;
  }

  getBillingLabelTypeCustomer() {
    return this.constant.billingLabelTypeCustomer;
  }

  getAccountingJournalSales() {
    return this.constant.accountingJournalSales;
  }

  getAccountingJournalSalary() {
    return this.constant.accountingJournalSalary;
  }

  getAccountingJournalPurchases() {
    return this.constant.accountingJournalPurchases;
  }

  getAccountingJournalMiscellaneousOperations() {
    return this.constant.accountingJournalMiscellaneousOperations;
  }

  getAccountingJournalANouveau() {
    return this.constant.accountingJournalANouveau;
  }

  getAccountingJournalBank() {
    return this.constant.accountingJournalBank;
  }

  getAccountingJournalCash() {
    return this.constant.accountingJournalCash;
  }

  getAccountingJournalBilan() {
    return this.constant.accountingJournalBilan;
  }

  getTiersTypeProspect() {
    return this.constant.tiersTypeProspect;
  }

  getTiersTypeClient() {
    return this.constant.tiersTypeClient;
  }

  getDocumentTypeDigital() {
    return this.constant.documentTypeDigital;
  }

  getDocumentTypePaper() {
    return this.constant.documentTypePaper;
  }

  getDocumentTypeBilling() {
    return this.constant.documentTypeBilling;
  }

  getDocumentTypeDunning() {
    return this.constant.documentTypeDunning;
  }
  getDocumentTypeSynthesisRbeSigned() {
    return this.constant.documentTypeSynthesisRbeSigned;
  }
  getDocumentTypeSynthesisRbeUnsigned() {
    return this.constant.documentTypeSynthesisRbeUnsigned;
  }

  getDocumentTypeRefund() {
    return this.constant.documentTypeRefund;
  }

  getDocumentTypeBillingClosure() {
    return this.constant.documentTypeBillingClosure;
  }

  getDocumentTypeProvisionnalReceipt() {
    return this.constant.documentTypeProvisionnalReceipt;
  }

  getAttachmentTypeKbis() {
    return this.constant.attachmentTypeKbis;
  }

  getAttachmentTypeCni() {
    return this.constant.attachmentTypeCni;
  }

  getAttachmentTypeLogo() {
    return this.constant.attachmentTypeLogo;
  }

  getAttachmentTypeProofOfAddress() {
    return this.constant.attachmentTypeProofOfAddress;
  }

  getAttachmentTypeInvoice() {
    return this.constant.attachmentTypeInvoice;
  }

  getAttachmentTypeProviderInvoice() {
    return this.constant.attachmentTypeProviderInvoice;
  }

  getAttachmentTypeCreditNote() {
    return this.constant.attachmentTypeCreditNote;
  }

  getAttachmentTypeKbisUpdated() {
    return this.constant.attachmentTypeKbisUpdated;
  }

  getAttachmentTypeDepositReceipt() {
    return this.constant.attachmentTypeDepositReceipt;
  }

  getAttachmentTypeRbe() {
    return this.constant.attachmentTypeRbe;
  }

  getAttachmentTypePublicationFlag() {
    return this.constant.attachmentTypePublicationFlag;
  }

  getAttachmentTypePublicationReceipt() {
    return this.constant.attachmentTypePublicationReceipt;
  }

  getAttachmentTypePublicationProof() {
    return this.constant.attachmentTypePublicationProof;
  }

  getAttachmentTypeJournal() {
    return this.constant.attachmentTypeJournal;
  }

  getAttachmentTypeBillingClosure() {
    return this.constant.attachmentTypeBillingClosure;
  }

  getAttachmentTypeProofReading() {
    return this.constant.attachmentTypeProofReading;
  }

  getAttachmentTypeAutomaticMail() {
    return this.constant.attachmentTypeAutomaticMail;
  }

  getAttachmentTypeAnnouncement() {
    return this.constant.attachmentTypeAnnouncement;
  }

  getAttachmentTypeComplexAnnouncement() {
    return this.constant.attachmentTypeComplexAnnouncement;
  }

  getAttachmentTypeContract() {
    return this.constant.attachmentTypeContract;
  }

  getAttachmentTypeTemplate() {
    return this.constant.attachmentTypeTemplate;
  }

  getAttachmentTypeQuotation() {
    return this.constant.attachmentTypeQuotation;
  }

  getCountryFrance() {
    return this.constant.countryFrance;
  }

  getPaymentDeadLineType30() {
    return this.constant.paymentDeadLineType30;
  }

  getCountryMonaco() {
    return this.constant.countryMonaco;
  }

  getBillingTypeDeboursNonTaxable() {
    return this.constant.billingTypeDeboursNonTaxable;
  }

  getBillingTypeCentralPayFees() {
    return this.constant.billingTypeCentralPayFees;
  }

  getBillingTypeRff() {
    return this.constant.billingTypeRff;
  }

  getBillingTypeLogo() {
    return this.constant.billingTypeLogo;
  }

  getBillingTypeRedactedByJss() {
    return this.constant.billingTypeRedactedByJss;
  }

  getBillingTypeBaloPackage() {
    return this.constant.billingTypeBaloPackage;
  }

  getBillingTypeBaloNormalization() {
    return this.constant.billingTypeBaloNormalization;
  }

  getBillingTypeBaloPublicationFlag() {
    return this.constant.billingTypeBaloPublicationFlag;
  }

  getBillingTypePublicationPaper() {
    return this.constant.billingTypePublicationPaper;
  }

  getBillingTypePublicationReceipt() {
    return this.constant.billingTypePublicationReceipt;
  }

  getBillingTypePublicationFlag() {
    return this.constant.billingTypePublicationFlag;
  }

  getBillingTypeBodaccFollowup() {
    return this.constant.billingTypeBodaccFollowup;
  }

  getBillingTypeBodaccFollowupAndRedaction() {
    return this.constant.billingTypeBodaccFollowupAndRedaction;
  }

  getBillingTypeNantissementDeposit() {
    return this.constant.billingTypeNantissementDeposit;
  }

  getBillingTypeSocialShareNantissementRedaction() {
    return this.constant.billingTypeSocialShareNantissementRedaction;
  }

  getBillingTypeBusinnessNantissementRedaction() {
    return this.constant.billingTypeBusinnessNantissementRedaction;
  }

  getBillingTypeSellerPrivilegeRedaction() {
    return this.constant.billingTypeSellerPrivilegeRedaction;
  }

  getBillingTypeTreatmentMultipleModiciation() {
    return this.constant.billingTypeTreatmentMultipleModiciation;
  }

  getBillingTypeVacationMultipleModification() {
    return this.constant.billingTypeVacationMultipleModification;
  }

  getBillingTypeRegisterPurchase() {
    return this.constant.billingTypeRegisterPurchase;
  }

  getBillingTypeRegisterInitials() {
    return this.constant.billingTypeRegisterInitials;
  }

  getBillingTypeRegisterShippingCosts() {
    return this.constant.billingTypeRegisterShippingCosts;
  }

  getBillingTypeDisbursement() {
    return this.constant.billingTypeDisbursement;
  }

  getBillingTypeFeasibilityStudy() {
    return this.constant.billingTypeFeasibilityStudy;
  }

  getBillingTypeChronopostFees() {
    return this.constant.billingTypeChronopostFees;
  }

  getBillingTypeConfrereFees() {
    return this.constant.billingTypeConfrereFees;
  }

  getBillingTypeShippingCosts() {
    return this.constant.billingTypeShippingCosts;
  }

  getBillingTypeApplicationFees() {
    return this.constant.billingTypeApplicationFees;
  }

  getBillingTypeBankCheque() {
    return this.constant.billingTypeBankCheque;
  }

  getBillingTypeComplexeFile() {
    return this.constant.billingTypeComplexeFile;
  }

  getBillingTypeBilan() {
    return this.constant.billingTypeBilan;
  }

  getProvisionTypeBilanPublication() {
    return this.constant.provisionTypeBilanPublication;
  }

  getProvisionTypeRegistrationAct() {
    return this.constant.provisionTypeRegistrationAct;
  }

  getProvisionTypeRbe() {
    return this.constant.provisionTypeRbe;
  }

  getProvisionFamilyTypeDeposit() {
    return this.constant.provisionFamilyTypeDeposit;
  }
  getBillingTypeInfogreffeDebour() {
    return this.constant.billingTypeInfogreffeDebour;
  }

  getBillingTypeEmolumentsDeGreffeDebour() {
    return this.constant.billingTypeEmolumentsDeGreffeDebour;
  }

  getBillingTypeDocumentScanning() {
    return this.constant.billingTypeDocumentScanning;
  }

  getBillingTypeEmergency() {
    return this.constant.billingTypeEmergency;
  }

  getBillingTypeRneUpdate() {
    return this.constant.billingTypeRneUpdate;
  }

  getBillingTypeVacationUpdateBeneficialOwners() {
    return this.constant.billingTypeVacationUpdateBeneficialOwners;
  }

  getBillingTypeFormalityAdditionalDeclaration() {
    return this.constant.billingTypeFormalityAdditionalDeclaration;
  }

  getBillingTypeCorrespondenceFees() {
    return this.constant.billingTypeCorrespondenceFees;
  }

  getBillingTypeDomiciliationContractTypeKeepMail() {
    return this.constant.billingTypeDomiciliationContractTypeKeepMail;
  }

  getBillingTypeDomiciliationContractTypeRouteEmail() {
    return this.constant.billingTypeDomiciliationContractTypeRouteEmail;
  }

  getBillingTypeDomiciliationContractTypeRouteMail() {
    return this.constant.billingTypeDomiciliationContractTypeRouteMail;
  }

  getBillingTypeDomiciliationContractTypeRouteEmailAndMail() {
    return this.constant.billingTypeDomiciliationContractTypeRouteEmailAndMail;
  }

  getBillingTypeSupplyFullBeCopy() {
    return this.constant.billingTypeSupplyFullBeCopy;
  }

  getStringNantissementDepositFormeJuridiqueCode() {
    return this.constant.stringNantissementDepositFormeJuridiqueCode;
  }
  getStrinSocialShareNantissementRedactionFormeJuridiqueCode() {
    return this.constant.strinSocialShareNantissementRedactionFormeJuridiqueCode;
  }
  getStringBusinnessNantissementRedactionFormeJuridiqueCode() {
    return this.constant.stringBusinnessNantissementRedactionFormeJuridiqueCode;
  }

  getPaymentTypeAccount() {
    return this.constant.paymentTypeAccount;
  }

  getPaymentTypeEspeces() {
    return this.constant.paymentTypeEspeces;
  }

  getPaymentTypeCheques() {
    return this.constant.paymentTypeCheques;
  }

  getPaymentTypeCB() {
    return this.constant.paymentTypeCB;
  }

  getPaymentTypeVirement() {
    return this.constant.paymentTypeVirement;
  }

  getPaymentTypePrelevement() {
    return this.constant.paymentTypePrelevement;
  }

  getRefundTypeVirement() {
    return this.constant.refundTypeVirement;
  }

  getSubscriptionPeriodType12M() {
    return this.constant.subscriptionPeriodType12M;
  }

  getLegalFormUnregistered() {
    return this.constant.legalFormUnregistered;
  }

  getJournalTypeSpel() {
    return this.constant.journalTypeSpel;
  }

  getJournalTypePaper() {
    return this.constant.journalTypePaper;
  }

  getConfrereJssSpel() {
    return this.constant.confrereJssSpel;
  }

  getDomiciliationContractTypeKeepMail() {
    return this.constant.domiciliationContractTypeKeepMail;
  }

  getDomiciliationContractTypeRouteMail() {
    return this.constant.domiciliationContractTypeRouteMail;
  }

  getDomiciliationContractTypeRouteEmail() {
    return this.constant.domiciliationContractTypeRouteEmail;
  }

  getDomiciliationContractTypeRouteEmailAndMail() {
    return this.constant.domiciliationContractTypeRouteEmailAndMail;
  }

  getMailRedirectionTypeOther() {
    return this.constant.mailRedirectionTypeOther;
  }

  getMailRedirectionTypeLegalGuardian() {
    return this.constant.mailRedirectionTypeLegalGuardian;
  }

  getMailRedirectionTypeActivity() {
    return this.constant.mailRedirectionTypeActivity;
  }

  getActTypeSeing() {
    return this.constant.actTypeSeing;
  }

  getActTypeAuthentic() {
    return this.constant.actTypeAuthentic;
  }

  getAssignationTypeEmployee() {
    return this.constant.assignationTypeEmployee;
  }

  getEmployeeBillingResponsible() {
    return this.constant.employeeBillingResponsible;
  }

  getEmployeeMailResponsible() {
    return this.constant.employeeMailResponsible;
  }

  getEmployeeSalesDirector() {
    return this.constant.employeeSalesDirector;
  }

  getEmployeeInvoiceReminderResponsible() {
    return this.constant.employeeInvoiceReminderResponsible;
  }

  getTransfertFundsTypePhysique() {
    return this.constant.transfertFundsTypePhysique;
  }

  getTransfertFundsTypeMoral() {
    return this.constant.transfertFundsTypeMoral;
  }

  getTransfertFundsTypeBail() {
    return this.constant.transfertFundsTypeBail;
  }

  getCompetentAuthorityInfogreffe() {
    return this.constant.competentAuthorityInfogreffe;
  }

  getCompetentAuthorityInpi() {
    return this.constant.competentAuthorityInpi;
  }

  getCompetentAuthorityTypeRcs() {
    return this.constant.competentAuthorityTypeRcs;
  }

  getCompetentAuthorityTypeCfp() {
    return this.constant.competentAuthorityTypeCfp;
  }

  getCompetentAuthorityTypeInsee() {
    return this.constant.competentAuthorityTypeInsee;
  }

  getCompetentAuthorityTypeCci() {
    return this.constant.competentAuthorityTypeCci;
  }

  getCompetentAuthorityTypeChambreMetier() {
    return this.constant.competentAuthorityTypeChambreMetier;
  }

  getCompetentAuthorityTypeChambreAgriculture() {
    return this.constant.competentAuthorityTypeChambreAgriculture;
  }

  getCompetentAuthorityTypeUrssaf() {
    return this.constant.competentAuthorityTypeUrssaf;
  }

  getCompetentAuthorityTypeDireccte() {
    return this.constant.competentAuthorityTypeDireccte;
  }

  getCompetentAuthorityTypePrefecture() {
    return this.constant.competentAuthorityTypePrefecture;
  }

  getCompetentAuthorityTypeSpfe() {
    return this.constant.competentAuthorityTypeSpfe;
  }

  getInvoiceStatusSend() {
    return this.constant.invoiceStatusSend;
  }

  getInvoiceStatusReceived() {
    return this.constant.invoiceStatusReceived;
  }

  getInvoiceStatusCreditNoteEmited() {
    return this.constant.invoiceStatusCreditNoteEmited;
  }

  getInvoiceStatusCreditNoteReceived() {
    return this.constant.invoiceStatusCreditNoteReceived;
  }

  getInvoiceStatusPayed() {
    return this.constant.invoiceStatusPayed;
  }

  getVatTwenty() {
    return this.constant.vatTwenty;
  }

  getVatZero() {
    return this.constant.vatZero;
  }

  getVatTwo() {
    return this.constant.vatTwo;
  }

  getVatDeductibleTwo() {
    return this.constant.vatDeductibleTwo;
  }

  getVatDeductible() {
    return this.constant.vatDeductible;
  }

  getDepartmentMartinique() {
    return this.constant.departmentMartinique;
  }

  getDepartmentGuadeloupe() {
    return this.constant.departmentGuadeloupe;
  }

  getDepartmentReunion() {
    return this.constant.departmentReunion;
  }

  getInvoiceStatusCancelled() {
    return this.constant.invoiceStatusCancelled;
  }

  getTypePersonnePersonnePhysique() {
    return this.constant.typePersonnePersonnePhysique;
  }

  getTypePersonnePersonneMorale() {
    return this.constant.typePersonnePersonneMorale;
  }

  getTypePersonneExploitation() {
    return this.constant.typePersonneExploitation;
  }

  getTypeFormaliteCessation() {
    return this.constant.typeFormaliteCessation;
  }

  getTypeFormaliteModification() {
    return this.constant.typeFormaliteModification;
  }

  getTypeFormaliteCreation() {
    return this.constant.typeFormaliteCreation;
  }

  getTypeFormaliteCorrection() {
    return this.constant.typeFormaliteCorrection;
  }

  getSalesSharedMailbox() {
    return this.constant.salesSharedMailbox;
  }

  getAccountingSharedMaiblox() {
    return this.constant.accountingSharedMaiblox;
  }

  getRecoverySharedMaiblox() {
    return this.constant.recoverySharedMaiblox;
  }

  getBillingClosureRecipientTypeOther() {
    return this.constant.billingClosureRecipientTypeOther;
  }

  getBillingClosureRecipientTypeResponsable() {
    return this.constant.billingClosureRecipientTypeResponsable;
  }

  getBillingClosureRecipientTypeClient() {
    return this.constant.billingClosureRecipientTypeClient;
  }

  getBillingClosureTypeAffaire() {
    return this.constant.billingClosureTypeAffaire;
  }

  getDeliveryServiceJss() {
    return this.constant.deliveryServiceJss;
  }

  getLanguageFrench() {
    return this.constant.languageFrench;
  }

  getPrincipalAccountingAccountProvider() {
    return this.constant.principalAccountingAccountProvider;
  }

  getPrincipalAccountingAccountCustomer() {
    return this.constant.principalAccountingAccountCustomer;
  }

  getPrincipalAccountingAccountDeposit() {
    return this.constant.principalAccountingAccountDeposit;
  }

  getPrincipalAccountingAccountLitigious() {
    return this.constant.principalAccountingAccountLitigious;
  }

  getPrincipalAccountingAccountSuspicious() {
    return this.constant.principalAccountingAccountSuspicious;
  }

  getPrincipalAccountingAccountDepositProvider() {
    return this.constant.principalAccountingAccountDepositProvider;
  }

  getPrincipalAccountingAccountProduct() {
    return this.constant.principalAccountingAccountProduct;
  }

  getPrincipalAccountingAccountCharge() {
    return this.constant.principalAccountingAccountCharge;
  }

  getPrincipalAccountingAccountBank() {
    return this.constant.principalAccountingAccountBank;
  }

  getPrincipalAccountingAccountWaiting() {
    return this.constant.principalAccountingAccountWaiting;
  }

  getAccountingAccountLost() {
    return this.constant.accountingAccountLost;
  }

  getAccountingAccountProfit() {
    return this.constant.accountingAccountProfit;
  }

  getAccountingAccountBankCentralPay() {
    return this.constant.accountingAccountBankCentralPay;
  }

  getAccountingAccountBankJss() {
    return this.constant.accountingAccountBankJss;
  }

  getAccountingAccountCaisse() {
    return this.constant.accountingAccountCaisse;
  }

  getCustomerOrderOriginWebSite() {
    return this.constant.customerOrderOriginWebSite;
  }

  getCustomerOrderOriginMyJss() {
    return this.constant.customerOrderOriginMyJss;
  }

  getCustomerOrderOriginOsiris() {
    return this.constant.customerOrderOriginOsiris;
  }

  getCentralPayProvider() {
    return this.constant.providerCentralPay;
  }

  getTiersFollowupTypeInvoiceReminder() {
    return this.constant.tiersFollowupTypeInvoiceReminder;
  }

  getTiersCategoryPresse() {
    return this.constant.tiersCategoryPresse;
  }

  getRffFrequencyAnnual() {
    return this.constant.rffFrequencyAnnual;
  }

  getRffFrequencyQuarterly() {
    return this.constant.rffFrequencyQuarterly;
  }

  getRffFrequencyMonthly() {
    return this.constant.rffFrequencyMonthly;
  }

  getServiceTypeOther() {
    return this.constant.serviceTypeOther;
  }

  getDateAccountingClosureForAll() {
    return this.constant.dateAccountingClosureForAll;
  }

  getDateAccountingClosureForAccountant() {
    return this.constant.dateAccountingClosureForAccountant;
  }

  getActiveDirectoryGroupFormalites() {
    return this.constant.activeDirectoryGroupFormalites;
  }

  getActiveDirectoryGroupSales() {
    return this.constant.activeDirectoryGroupSales;
  }

  getActiveDirectoryGroupFacturation() {
    return this.constant.activeDirectoryGroupFacturation;
  }
  getFurtherInformationServiceFieldType() {
    return this.constant.furtherInformationServiceFieldType;
  }

  getResponsableDummyCustomerFrance() {
    return this.constant.responsableDummyCustomerFrance;
  }

  getProvisionScreenTypeAnnouncement() {
    return this.constant.provisionScreenTypeAnnouncement;
  }

  getCategoryInterview() {
    return this.constant.categoryInterview;
  }

  getCategoryPodcast() {
    return this.constant.categoryPodcast;
  }

  getCategoryArticle() {
    return this.constant.categoryArticle;
  }

  getCategorySerie() {
    return this.constant.categorySerie;
  }
}
