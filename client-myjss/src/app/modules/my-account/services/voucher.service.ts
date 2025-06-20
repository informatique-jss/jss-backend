import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../main/services/appRest.service";
import { Voucher } from "../model/Voucher";

@Injectable({
  providedIn: 'root'
})
export class VoucherService extends AppRestService<Voucher> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  applyVoucher(voucher: Voucher) {
    return this.get(new HttpParams().set("voucherCode", voucher.code), "voucher/apply");
  }
}
