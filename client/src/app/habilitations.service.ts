import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class HabilitationsService {

  constructor(http: HttpClient
  ) {
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

}



