import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { BillingType } from '../../tiers/model/BillingType';

@Injectable({
  providedIn: 'root'
})
export class BillingTypeService extends AppRestService<BillingType>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getBillingTypes() {
    return this.getList(new HttpParams(), "billingTypes");
  }

}
