import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { BillingItem } from '../../tiers/model/BillingItem';

@Injectable({
  providedIn: 'root'
})
export class BillingItemService extends AppRestService<BillingItem>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getBillingItems() {
    return this.getList(new HttpParams(), "billing-items");
  }

}
