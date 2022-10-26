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
      let constant = JSON.parse(a!) as Constant;
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

}
