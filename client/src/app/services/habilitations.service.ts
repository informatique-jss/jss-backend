import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ACCOUNTING, ACCOUNTING_RESPONSIBLE, ADMINISTRATEURS, LoginService } from '../routing/login-dialog/login.service';

@Injectable({
  providedIn: 'root'
})
export class HabilitationsService {
  constructor(http: HttpClient,
    private loginService: LoginService,
  ) {
  }

  canViewDashboardModule() {
    return true;
  }

  canViewReportingModule() {
    return true;
  }

  canViewTiersModule() {
    return true;
  }

  canViewConfrereModule() {
    return true;
  }

  canViewCompetentAuthorityModule() {
    return true;
  }

  canViewQuotationModule() {
    return true;
  }

  canViewCustomerOrderModule() {
    return true;
  }

  canViewAdministrationModule() {
    return this.loginService.hasGroup([ADMINISTRATEURS])
  }

  canViewAccountingModule() {
    return true;
  }

  canViewInvoiceModule() {
    return true;
  }

  canViewAffaireModule() {
    return true;
  }

  canViewProvisionModule() {
    return true;
  }

  canViewLogModule() {
    return this.loginService.hasGroup([ADMINISTRATEURS]);
  }

  canAddNewInvoice() {
    return this.loginService.hasGroup([ACCOUNTING, ACCOUNTING_RESPONSIBLE])
  }

  canAddNewAccountingRecord() {
    return this.loginService.hasGroup([ACCOUNTING, ACCOUNTING_RESPONSIBLE])
  }

  canAddNewCashPayment() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE])
  }

  canDisplayBilan() {
    return this.loginService.hasGroup([ACCOUNTING, ACCOUNTING_RESPONSIBLE])
  }

  canDisplayProfitLost() {
    return this.loginService.hasGroup([ACCOUNTING, ACCOUNTING_RESPONSIBLE])
  }

  canImportOfxFile() {
    return this.loginService.hasGroup([ACCOUNTING, ACCOUNTING_RESPONSIBLE])
  }

  canModifyPaymentAssociation() {
    return this.loginService.hasGroup([ACCOUNTING, ACCOUNTING_RESPONSIBLE])
  }

}



