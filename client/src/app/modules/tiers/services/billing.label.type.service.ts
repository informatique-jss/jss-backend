import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { BillingLabelType } from '../model/BillingLabelType';

@Injectable({
  providedIn: 'root'
})
export class BillingLabelTypeService extends AppRestService<BillingLabelType>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getBillingLabelTypes() {
    return this.getList(new HttpParams(), "billing-label-types");
  }

}
