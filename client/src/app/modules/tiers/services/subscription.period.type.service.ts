import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { SubscriptionPeriodType } from '../../tiers/model/SubscriptionPeriodType';

@Injectable({
  providedIn: 'root'
})
export class SubscriptionPeriodTypeService extends AppRestService<SubscriptionPeriodType>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getSubscriptionPeriodTypes() {
    return this.getList(new HttpParams(), "subscription-period-types");
  }

}
