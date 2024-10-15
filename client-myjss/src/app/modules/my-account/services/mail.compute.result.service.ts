import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { CustomerOrder } from '../model/CustomerOrder';
import { MailComputeResult } from '../model/MailComputeResult';
import { Quotation } from '../model/Quotation';

@Injectable({
  providedIn: 'root'
})
export class MailComputeResultService extends AppRestService<MailComputeResult> {

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getMailComputeResultForBillingForCustomerOrder(orderId: number) {
    return this.get(new HttpParams().set("customerOrderId", orderId), "mail/billing/compute/order");
  }

  getMailComputeResultForBillingForQuotation(quotation: Quotation) {
    return this.get(new HttpParams().set("quotationId", quotation.id), "mail/billing/compute/quotation");
  }

  getMailComputeResultForDigitalForCustomerOrder(order: CustomerOrder) {
    return this.get(new HttpParams().set("customerOrderId", order.id), "mail/digital/compute/order");
  }

  getMailComputeResultForDigitalForQuotation(quotation: Quotation) {
    return this.get(new HttpParams().set("quotationId", quotation.id), "mail/digital/compute/quotation");
  }
}
