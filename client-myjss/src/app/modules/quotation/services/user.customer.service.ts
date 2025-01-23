import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { UserCustomerOrder } from '../model/UserCustomerOrder';

@Injectable({
  providedIn: 'root'
})
export class UserCustomerOrderService extends AppRestService<UserCustomerOrder> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  completePricingOfUserCustomerOrder(order: UserCustomerOrder) {
    return this.postItem(new HttpParams(), "order/user/pricing", order);
  }

  saveOrder(order: UserCustomerOrder) {
    return this.postItem(new HttpParams(), "order/user/save", order);
  }
}
