import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { BillingClosureRecipientType } from '../model/BillingClosureRecipientType';

@Injectable({
  providedIn: 'root'
})
export class BillingClosureRecipientTypeService extends AppRestService<BillingClosureRecipientType>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getBillingClosureRecipientTypes() {
    return this.getListCached(new HttpParams(), "billing-closure-recipient-types");
  }

  addOrUpdateBillingClosureRecipientType(billingClosureRecipientType: BillingClosureRecipientType) {
    this.clearListCache(new HttpParams(), "billing-closure-recipient-types");
    return this.addOrUpdate(new HttpParams(), "billing-closure-recipient-type", billingClosureRecipientType, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
