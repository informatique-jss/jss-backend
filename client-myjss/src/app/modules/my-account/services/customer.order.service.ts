import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { CustomerOrder } from '../model/CustomerOrder';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderService extends AppRestService<CustomerOrder> {
  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  searchOrdersForCurrentUser(customerOrderStatus: string[], page: number, sorter: string) {
    return this.postList(new HttpParams().set("page", page).set("sortBy", sorter), "order/search/current", customerOrderStatus);
  }

  getCustomerOrder(customerOrderId: number) {
    return this.get(new HttpParams().set("customerOrderId", customerOrderId), 'order');
  }

  getCustomerOrdersForAffaireAndCurrentUser(idAffaire: number) {
    return this.getList(new HttpParams().set("idAffaire", idAffaire), 'order/search/affaire');
  }

  downloadInvoice(order: CustomerOrder) {
    this.downloadGet(new HttpParams().set("customerOrderId", order.id + ""), "attachment/invoice/download");
  }

  getCustomerOrderForQuotation(idQuotation: number) {
    return this.get(new HttpParams().set("idQuotation", idQuotation), 'quotation/order');
  }
}
