import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { CustomerOrderOrigin } from '../../miscellaneous/model/CustomerOrderOrigin';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderOriginService extends AppRestService<CustomerOrderOrigin>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getCustomerOrderOrigins() {
    return this.getList(new HttpParams(), "customer-order-origins");
  }
  
   addOrUpdateCustomerOrderOrigin(customerOrderOrigin: CustomerOrderOrigin) {
    return this.addOrUpdate(new HttpParams(), "customer-order-origin", customerOrderOrigin, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
