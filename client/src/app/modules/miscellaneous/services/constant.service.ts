import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Constant } from '../../miscellaneous/model/Constant';

@Injectable({
  providedIn: 'root'
})
export class ConstantService extends AppRestService<Constant>{

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

  getAttachmentTypeKbisUpdated() {
    return this.constant.attachmentTypeKbisUpdated;
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

  getCountryFrance() {
    return this.constant.countryFrance;
  }

  getCountryMonaco() {
    return this.constant.countryMonaco;
  }

  getBillingTypeCentralPayFees() {
    return this.constant.billingTypeCentralPayFees;
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

  getBillingTypeDocumentScanning() {
    return this.constant.billingTypeDocumentScanning;
  }

  getBillingTypeEmergency() {
    return this.constant.billingTypeEmergency;
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

  getPaymentTypeEspeces() {
    return this.constant.paymentTypeEspeces;
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

  getConfrereJssPaper() {
    return this.constant.confrereJssPaper;
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

  getBodaccPublicationTypeMerging() {
    return this.constant.bodaccPublicationTypeMerging;
  }

  getBodaccPublicationTypeSplit() {
    return this.constant.bodaccPublicationTypeSplit;
  }

  getBodaccPublicationTypePartialSplit() {
    return this.constant.bodaccPublicationTypePartialSplit;
  }

  getBodaccPublicationTypePossessionDispatch() {
    return this.constant.bodaccPublicationTypePossessionDispatch;
  }

  getBodaccPublicationTypeEstateRepresentativeDesignation() {
    return this.constant.bodaccPublicationTypeEstateRepresentativeDesignation;
  }

  getBodaccPublicationTypeSaleOfBusiness() {
    return this.constant.bodaccPublicationTypeSaleOfBusiness;
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

  getCompetentAuthorityTypeRcs() {
    return this.constant.competentAuthorityTypeRcs;
  }

  getCompetentAuthorityTypeCfp() {
    return this.constant.competentAuthorityTypeCfp;
  }

  getInvoiceStatusSend() {
    return this.constant.invoiceStatusSend;
  }

  getInvoiceStatusReceived() {
    return this.constant.invoiceStatusReceived;
  }

  getPaymentWayInbound() {
    return this.constant.paymentWayInbound;
  }

  getInvoiceStatusPayed() {
    return this.constant.invoiceStatusPayed;
  }

  getVatTwenty() {
    return this.constant.vatTwenty;
  }

  getVatEight() {
    return this.constant.vatEight;
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

  getPrincipalAccountingAccountLost() {
    return this.constant.principalAccountingAccountLost;
  }

  getPrincipalAccountingAccountProfit() {
    return this.constant.principalAccountingAccountProfit;
  }

  getAccountingAccountBankCentralPay() {
    return this.constant.accountingAccountBankCentralPay;
  }

  getAccountingAccountBankJss() {
    return this.constant.accountingAccountBankJss;
  }
}
