import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from './appRest.service';
import { Constant } from './Constant';

@Injectable({
  providedIn: 'root'
})
export class ConstantService extends AppRestService<Constant> {

  constructor(http: HttpClient) {
    super(http, "profile");
  }

  constant: Constant = {} as Constant;

  getConstants() {
    return this.get(new HttpParams(), "constants");
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

  getDocumentTypeDigital() {
    return this.constant.documentTypeDigital;
  }

  getDocumentTypePaper() {
    return this.constant.documentTypePaper;
  }

  getDocumentTypeBilling() {
    return this.constant.documentTypeBilling;
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

}
