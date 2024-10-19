import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { BillingClosureReceiptValue } from '../model/BillingClosureReceiptValue';

@Injectable({
  providedIn: 'root'
})
export class BillingClosureService extends AppRestService<BillingClosureReceiptValue> {

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getBillingClosureReceiptValueForResponsable(responsableId: number, isOrderingByEventDate: boolean) {
    return this.getListCached(new HttpParams().set("responsableId", responsableId).set("isOrderingByEventDate", isOrderingByEventDate), "billing-closure");
  }
}
