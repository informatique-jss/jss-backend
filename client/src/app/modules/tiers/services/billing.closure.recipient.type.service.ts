import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { BillingClosureRecipientType } from '../model/BillingClosureRecipientType';

@Injectable({
  providedIn: 'root'
})
export class BillingClosureRecipientTypeService extends AppRestService<BillingClosureRecipientType>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getBillingClosureRecipientTypes() {
    return this.getList(new HttpParams(), "billing-closure-recipient-types");
  }

}
