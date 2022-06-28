import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { BillingItem } from '../../miscellaneous/model/BillingItem';

@Injectable({
  providedIn: 'root'
})
export class BillingItemService extends AppRestService<BillingItem>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getBillingItems() {
    return this.getList(new HttpParams(), "billing-items");
  }
  
   addOrUpdateBillingItem(billingItem: BillingItem) {
    return this.addOrUpdate(new HttpParams(), "billing-item", billingItem, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
