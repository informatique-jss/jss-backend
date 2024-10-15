import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../../libs/appRest.service";
import { CustomerOrder } from "../model/CustomerOrder";
import { InvoicingSummary } from "../model/InvoicingSummary";


@Injectable({
  providedIn: 'root'
})
export class InvoicingSummaryService extends AppRestService<InvoicingSummary> {

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getInvoicingSummaryForCustomerOrder(customerOrder: CustomerOrder) {
    return this.get(new HttpParams().set("customerOrderId", customerOrder.id), "invoice/summary/order");
  }
}
