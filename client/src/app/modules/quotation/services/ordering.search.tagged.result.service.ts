import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { CustomerOrder } from '../model/CustomerOrder';
import { OrderingSearch } from '../model/OrderingSearch';
import { OrderingSearchResult } from '../model/OrderingSearchResult';
import { Quotation } from '../model/Quotation';
import { OrderingSearchTaggedResult } from '../model/OrderingSearchTaggedResult';
import { OrderingSearchTagged } from '../model/OrderingSearchTagged';

@Injectable({
  providedIn: 'root'
})
export class OrderingSearchTaggedResultService extends AppRestService<OrderingSearchTaggedResult> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getOrdersTagged(orderingSearchTagged: OrderingSearchTagged) {
    return this.postList(new HttpParams(), "order-tagged/search", orderingSearchTagged);
  }
}
