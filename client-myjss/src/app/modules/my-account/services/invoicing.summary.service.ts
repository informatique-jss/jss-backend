import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../main/services/appRest.service";
import { InvoicingSummary } from "../model/InvoicingSummary";


@Injectable({
  providedIn: 'root'
})
export class InvoicingSummaryService extends AppRestService<InvoicingSummary> {

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getInvoicingSummaryForCustomerOrder(customerOrderId: number) {
    return this.get(new HttpParams().set("customerOrderId", customerOrderId), "invoice/summary/order");
  }

  getInvoicingSummaryForQuotation(quotationId: number) {
    return this.get(new HttpParams().set("quotationId", quotationId), "invoice/summary/quotation");
  }
}
