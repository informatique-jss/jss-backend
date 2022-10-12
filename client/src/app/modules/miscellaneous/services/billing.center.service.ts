import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { BillingCenter } from '../../miscellaneous/model/BillingCenter';

@Injectable({
  providedIn: 'root'
})
export class BillingCenterService extends AppRestService<BillingCenter>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getBillingCenters() {
    return this.getList(new HttpParams(), "billing-centers");
  }

  addOrUpdateBillingCenter(billingCenter: BillingCenter) {
    return this.addOrUpdate(new HttpParams(), "billing-center", billingCenter, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
