import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "src/app/services/appRest.service";
import { CustomerOrder } from '../../quotation/model/CustomerOrder';
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

  getAllVouchers() {
    return this.getList(new HttpParams(), "vouchers");
  }

  getActiveVouchers() {
    return this.getList(new HttpParams(), "vouchers/active");
  }

  deleteVoucher(voucher: Voucher) {
    return this.get(new HttpParams().set("idVoucher", voucher.id), "voucher/delete");
  }

  getVouchersBySearchCode(code: string) {
    return this.getList(new HttpParams().set("code", code), "vouchers/search");
  }

  applyVoucher(customerOrder: CustomerOrder, voucher: Voucher) {
    return this.get(new HttpParams().set("customerOrderId", customerOrder.id).set("voucherCode", voucher.code), "voucher/apply");
  }
}
