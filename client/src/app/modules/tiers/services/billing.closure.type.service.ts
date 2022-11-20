import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { BillingClosureType } from '../../tiers/model/BillingClosureType';

@Injectable({
  providedIn: 'root'
})
export class BillingClosureTypeService extends AppRestService<BillingClosureType>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getBillingClosureTypes() {
    return this.getListCached(new HttpParams(), "billing-closure-types");
  }

  addOrUpdateBillingClosureType(billingClosureType: BillingClosureType) {
    this.clearListCache(new HttpParams(), "billing-closure-types");
    return this.addOrUpdate(new HttpParams(), "billing-closure-type", billingClosureType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
