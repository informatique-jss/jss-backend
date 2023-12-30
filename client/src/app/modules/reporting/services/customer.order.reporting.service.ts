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

  getCustomerOrderReporting(columns: string[]) {
    return this.getList(new HttpParams().set("columns", columns.join(",")), "customer-order");
  }

}
