import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { CustomerOrder } from '../model/CustomerOrder';
import { Payment } from '../model/Payment';

@Injectable({
  providedIn: 'root'
})
export class PaymentService extends AppRestService<Payment> {
  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getApplicablePaymentsForCustomerOrder(order: CustomerOrder) {
    return this.getList(new HttpParams().set("customerOrderId", order.id), "order/list");
  }
}
