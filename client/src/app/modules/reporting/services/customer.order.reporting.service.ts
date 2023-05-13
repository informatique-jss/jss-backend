import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { CustomerOrderReporting } from '../model/CustomerOrderReporting';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderReportingService extends AppRestService<CustomerOrderReporting>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getCustomerOrderReporting() {
    return this.getList(new HttpParams(), "customer-order");
  }

  getCustomerOrderReportingForTiers(tiersId: number) {
    return this.getList(new HttpParams().set("tiersId", tiersId), "customer-order/tiers");
  }
}
