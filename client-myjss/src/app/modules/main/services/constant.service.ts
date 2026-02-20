import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { Observable, of, tap } from 'rxjs';
import { Constant, globalConstantCache } from '../model/Constant';
import { AppRestService } from './appRest.service';
import { PlatformService } from './platform.service';

@Injectable({
  providedIn: 'root',
})
export class ConstantService extends AppRestService<Constant> {

  constructor(http: HttpClient, private platformService: PlatformService) {
    super(http, "quotation");
  }

  constant: Constant = {} as Constant;

  getConstants() {
    return this.get(new HttpParams(), "constants");
  }

  initConstant() {
    if (this.platformService.isServer()) {
      if (globalConstantCache.data) {
        this.constant = globalConstantCache.data;
        return;
      }
    }

    if (this.constant && this.constant.id) {
      return;
    }

    if (this.platformService.isBrowser() && this.platformService.getNativeLocalStorage()!.getItem('constants') != null) {
      let a = this.platformService.getNativeLocalStorage()!.getItem('constants');
      this.constant = JSON.parse(a!) as Constant;
    }

    this.getConstants().subscribe(response => {
      this.constant = response;
      globalConstantCache.data = response;
      if (this.platformService.isBrowser())
        this.platformService.getNativeLocalStorage()!.setItem('constants', JSON.stringify(this.constant));
    });
  }

  initConstantFromResolver(): Observable<Constant> {
    if (this.platformService.isServer()) {
      if (globalConstantCache.data) {
        this.constant = globalConstantCache.data;
        return of(this.constant);
      }
    }

    if (this.constant && this.constant.id) {
      return of(this.constant);
    }

    if (this.platformService.isBrowser()) {
      const stored = this.platformService.getNativeLocalStorage()?.getItem('constants');
      if (stored) {
        this.constant = JSON.parse(stored) as Constant;
        return of(this.constant);
      }
    }

    return this.getConstants().pipe(
      tap(response => {
        this.constant = response;
        globalConstantCache.data = response;
        if (this.platformService.isBrowser()) {
          this.platformService.getNativeLocalStorage()?.setItem('constants', JSON.stringify(this.constant));
        }
      })
    );
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

  getCountryFrance() {
    return this.constant.countryFrance;
  }

  getResponsableDummyCustomerFrance() {
    return this.constant.responsableDummyCustomerFrance;
  }

  getMyJssCategoryAnnouncement() {
    return this.constant.myJssCategoryAnnouncement;
  }

  getMyJssCategoryFormality() {
    return this.constant.myJssCategoryFormality;
  }

  getMyJssCategoryDomiciliation() {
    return this.constant.myJssCategoryDomiciliation;
  }

  getMyJssCategoryApostille() {
    return this.constant.myJssCategoryApostille;
  }

  getMyJssCategoryDocument() {
    return this.constant.myJssCategoryDocument;
  }

  getServiceFamilyGroupAnnouncement() {
    return this.constant.serviceFamilyGroupAnnouncement;
  }

  getServiceFamilyGroupFormality() {
    return this.constant.serviceFamilyGroupFormality;
  }

  getServiceFamilyGroupOther() {
    return this.constant.serviceFamilyGroupOther;
  }

  getCategoryArticle() {
    return this.constant.categoryArticle;
  }

  getCategoryExclusivity() {
    return this.constant.categoryExclusivity;
  }

  getAttachmentTypeApplicationCv() {
    return this.constant.attachmentTypeApplicationCv;
  }

  getLanguageFrench() {
    return this.constant.languageFrench;
  }

  getDomiciliationContractTypeRouteEmailAndMail() {
    return this.constant.domiciliationContractTypeRouteEmailAndMail;
  }

  getDomiciliationContractTypeRouteMail() {
    return this.constant.domiciliationContractTypeRouteMail;
  }

  getMailRedirectionTypeOther() {
    return this.constant.mailRedirectionTypeOther;
  }

  getServiceTypeAnnualSubscription() {
    return this.constant.serviceTypeAnnualSubscription;
  }

  getServiceTypeEnterpriseAnnualSubscription() {
    return this.constant.serviceTypeEnterpriseAnnualSubscription;
  }

  getServiceTypeMonthlySubscription() {
    return this.constant.serviceTypeMonthlySubscription;
  }

  getServiceTypeKioskNewspaperBuy() {
    return this.constant.serviceTypeKioskNewspaperBuy;
  }

  getServiceTypeUniqueArticleBuy() {
    return this.constant.serviceTypeUniqueArticleBuy;
  }

  getProvisionTypeRbe() {
    return this.constant.provisionTypeRbe;
  }
}

@Injectable({ providedIn: 'root' })
export class ConstantsResolver implements Resolve<any> {
  constructor(private constantsService: ConstantService) { }

  resolve(): Observable<any> {
    return this.constantsService.initConstantFromResolver();
  }
}
