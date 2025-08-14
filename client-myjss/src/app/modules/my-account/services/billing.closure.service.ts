import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { BillingClosureReceiptValue } from '../model/BillingClosureReceiptValue';

@Injectable({
  providedIn: 'root'
})
export class BillingClosureService extends AppRestService<BillingClosureReceiptValue> {

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getBillingClosureReceiptValueForResponsable(responsableId: number, isOrderingByEventDate: boolean, isDesc: boolean) {
    return this.getList(new HttpParams().set("responsableId", responsableId).set("isOrderingByEventDate", isOrderingByEventDate).set("isDesc", isDesc), "billing-closure");
  }

  downloadBillingClosureReceiptValueForResponsable(responsableId: number) {
    return this.downloadGet(new HttpParams().set("responsableId", responsableId), "billing-closure/download");
  }
}
