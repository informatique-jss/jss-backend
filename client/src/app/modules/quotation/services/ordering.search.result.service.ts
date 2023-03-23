import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { OrderingSearch } from '../model/OrderingSearch';
import { OrderingSearchResult } from '../model/OrderingSearchResult';
import { Quotation } from '../model/Quotation';

@Injectable({
  providedIn: 'root'
})
export class OrderingSearchResultService extends AppRestService<OrderingSearchResult>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getOrders(orderingSearch: OrderingSearch) {
    return this.postList(new HttpParams(), "order/search", orderingSearch);
  }

  getCustomerOrderForQuotation(quotation: Quotation) {
    return this.getList(new HttpParams().set("idQuotation", quotation.id), "customer-order/quotation");
  }


}
