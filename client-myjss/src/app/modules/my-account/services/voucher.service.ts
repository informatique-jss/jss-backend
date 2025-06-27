import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";

import { AppRestService } from "../../main/services/appRest.service";
import { CustomerOrder } from "../model/CustomerOrder";
import { Quotation } from "../model/Quotation";
import { Voucher } from "../model/Voucher";

@Injectable({
  providedIn: 'root'
})
export class VoucherService extends AppRestService<Voucher> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getVoucher(id: number) {
    return this.get(new HttpParams().set("idVoucher", id), "voucher")
  }

  removeVoucherQuotation(quotation: Quotation) {
    return this.get(new HttpParams().set("quotationId", quotation.id), 'voucher/delete/quotation');
  }

  removeVoucherOrder(customerOrder: CustomerOrder) {
    return this.get(new HttpParams().set("customerOrderId", customerOrder.id), 'voucher/delete/order');
  }

  getVouchersBySearchCode(code: string) {
    return this.getList(new HttpParams().set("code", code), "vouchers/search");
  }

  checkVoucherOrderForUser(customerOrder: CustomerOrder, voucherCode: string) {
    return this.get(new HttpParams().set("customerOrderId", customerOrder.id).set("voucherCode", voucherCode), "voucher/order-user/apply");
  }

  checkVoucherQuotationForUser(quotation: Quotation, voucherCode: string) {
    return this.get(new HttpParams().set("quotationId", quotation.id).set("voucherCode", voucherCode), "voucher/quotation-user/apply");
  }

  checkVoucherOrder(customerOrder: CustomerOrder, voucherCode: string) {
    return this.postItem(new HttpParams().set("voucherCode", voucherCode), "voucher/order/apply", customerOrder);
  }

  checkVoucherQuotation(quotation: Quotation, voucherCode: string) {
    return this.postItem(new HttpParams().set("voucherCode", voucherCode), "voucher/quotation/apply", quotation);
  }
}
