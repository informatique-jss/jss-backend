import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ADMINISTRATEURS, LoginService } from '../routing/login-dialog/login.service';

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

  canViewTiersModule() {
    return true;
  }

  canViewQuotationModule() {
    return true;
  }

  canViewCustomerOrderModule() {
    return true;
  }

  canViewAdministrationModule() {
    return true;
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

  canViewJournalModule() {
    return true;
  }

  canViewLogModule() {
    return this.loginService.hasGroup([ADMINISTRATEURS]);
  }

}



