import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { BillingType } from '../../miscellaneous/model/BillingType';

@Injectable({
  providedIn: 'root'
})
export class BillingTypeService extends AppRestService<BillingType>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getBillingTypes() {
    return this.getListCached(new HttpParams(), "billing-types");
  }

  addOrUpdateBillingType(billingType: BillingType) {
    this.clearListCache(new HttpParams(), "billing-types");
    return this.addOrUpdate(new HttpParams(), "billing-type", billingType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
