import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { CustomerOrderDto } from '../model/CustomerOrderDto';
import { CustomerOrderSearch } from '../model/CustomerOrderSearch';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderService extends AppRestService<CustomerOrderDto> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  searchCustomerOrder(customerOrderSearch: CustomerOrderSearch) {
    return this.postList(new HttpParams(), "customer-order/search/v2", customerOrderSearch);
  }
}
