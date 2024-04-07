import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { CustomerOrderFrequency } from '../../miscellaneous/model/CustomerOrderFrequency';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderFrequencyService extends AppRestService<CustomerOrderFrequency>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getCustomerOrderFrequencies() {
    return this.getListCached(new HttpParams(), "customer-order-frequencies");
  }

  addOrUpdateCustomerOrderFrequency(customerOrderFrequency: CustomerOrderFrequency) {
    return this.addOrUpdate(new HttpParams(), "customer-order-frequency", customerOrderFrequency, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
