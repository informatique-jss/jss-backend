import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { BillingLabelType } from '../../tiers/model/BillingLabelType';

@Injectable({
  providedIn: 'root'
})
export class BillingLabelTypeService extends AppRestService<BillingLabelType>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getBillingLabelTypes() {
    return this.getListCached(new HttpParams(), "billing-label-types");
  }

  addOrUpdateBillingLabelType(billingLabelType: BillingLabelType) {
    this.clearListCache(new HttpParams(), "billing-label-types");
    return this.addOrUpdate(new HttpParams(), "billing-label-type", billingLabelType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
