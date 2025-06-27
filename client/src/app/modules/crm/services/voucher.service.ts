import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "src/app/services/appRest.service";
import { CustomerOrder } from "../../quotation/model/CustomerOrder";
import { Quotation } from "../../quotation/model/Quotation";
import { Voucher } from "../model/Voucher";

@Injectable({
  providedIn: 'root'
})
export class VoucherService extends AppRestService<Voucher> {

  constructor(http: HttpClient) {
    super(http, "crm");
  }

  addOrUpdateVoucher(voucher: Voucher) {
    return this.postItem(new HttpParams(), "voucher", voucher);
  }

  getVoucher(id: number) {
    return this.get(new HttpParams().set("idVoucher", id), "voucher")
  }

  getAllVouchers(isDisplayOnlyActiveVouchers: boolean) {
    return this.getList(new HttpParams().set("isDisplayOnlyActiveVouchers", isDisplayOnlyActiveVouchers), "vouchers");
  }

  deleteVoucher(voucher: Voucher) {
    return this.get(new HttpParams().set("idVoucher", voucher.id), "voucher/delete");
  }

  getVouchersBySearchCode(code: string) {
    return this.getList(new HttpParams().set("code", code), "vouchers/search");
  }

  checkVoucherOrder(customerOrder: CustomerOrder, voucherCode: string) {
    return this.get(new HttpParams().set("customerOrderId", customerOrder.id).set("voucherCode", voucherCode), "voucher/order-user/apply");
  }
  checkVoucherQuotation(quotation: Quotation, voucherCode: string) {
    return this.get(new HttpParams().set("quotationId", quotation.id).set("voucherCode", voucherCode), "voucher/quotation-user/apply");
  }
}
