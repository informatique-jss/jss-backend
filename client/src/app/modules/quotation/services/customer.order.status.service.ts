import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { CustomerOrderStatus } from '../model/CustomerOrderStatus';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderStatusService extends AppRestService<CustomerOrderStatus>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getCustomerOrderStatus() {
    return this.getList(new HttpParams(), "customer-order-status");
  }

}
