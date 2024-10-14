import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../../libs/appRest.service";
import { CustomerOrder } from "../model/CustomerOrder";
import { InvoiceLabelResult } from "../model/InvoiceLabelResult";
import { Quotation } from "../model/Quotation";


@Injectable({
  providedIn: 'root'
})
export class InvoiceLabelResultService extends AppRestService<InvoiceLabelResult> {

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getInvoiceLabelComputeResultForCustomerOrder(customerOrder: CustomerOrder) {
    return this.get(new HttpParams().set("customerOrderId", customerOrder.id), "invoice/label/compute/order");
  }

  getInvoiceLabelComputeResultForQuotation(quotation: Quotation) {
    return this.get(new HttpParams().set("quotationId", quotation.id), "invoice/label/compute/quotation");
  }

  getPhysicalMailComputeResultForBillingForCustomerOrder(customerOrder: CustomerOrder) {
    return this.get(new HttpParams().set("customerOrderId", customerOrder.id), "invoice/label/physical/compute/order");
  }

  getPhysicalMailComputeResultForBillingForQuotation(quotation: Quotation) {
    return this.get(new HttpParams().set("quotationId", quotation.id), "invoice/label/physical/compute/quotation");
  }

}
