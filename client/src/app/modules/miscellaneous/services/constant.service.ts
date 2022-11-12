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

  getAccountingJournalANouveau() {
    return this.constant.accountingJournalANouveau;
  }

  getTiersTypeProspect() {
    return this.constant.tiersTypeProspect;
  }

  getDocumentTypePublication() {
    return this.constant.documentTypePublication;
  }

  getDocumentTypeCfe() {
    return this.constant.documentTypeCfe;
  }

  getDocumentTypeKbis() {
    return this.constant.documentTypeKbis;
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

  getDocumentTypeProofReading() {
    return this.constant.documentTypeProofReading;
  }

  getDocumentTypePublicationCertificate() {
    return this.constant.documentTypePublicationCertificate;
  }

  getDocumentTypeQuotation() {
    return this.constant.documentTypeQuotation;
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

  getCountryFrance() {
    return this.constant.countryFrance;
  }

  getCountryMonaco() {
    return this.constant.countryMonaco;
  }

  getBillingTypeLogo() {
    return this.constant.billingTypeLogo;
  }

  getQuotationLabelTypeOther() {
    return this.constant.quotationLabelTypeOther;
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

  getConfrereJss() {
    return this.constant.confrereJss;
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

  getFormeJuridiqueEntrepreneurIndividuel() {
    return this.constant.formeJuridiqueEntrepreneurIndividuel;
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
}
