import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { SubscriptionPeriodType } from '../../tiers/model/SubscriptionPeriodType';

@Injectable({
  providedIn: 'root'
})
export class SubscriptionPeriodTypeService extends AppRestService<SubscriptionPeriodType>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getSubscriptionPeriodTypes() {
    return this.getListCached(new HttpParams(), "subscription-period-types");
  }

  addOrUpdateSubscriptionPeriodType(subscriptionPeriodType: SubscriptionPeriodType) {
    this.clearListCache(new HttpParams(), "subscription-period-types");
    return this.addOrUpdate(new HttpParams(), "subscription-period-type", subscriptionPeriodType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
