import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Refund } from '../../invoicing/model/Refund';

@Injectable({
  providedIn: 'root'
})
export class RefundService extends AppRestService<Refund> {

  constructor(http: HttpClient) {
    super(http, "refund");
  }

  getRefunds() {
    return this.getList(new HttpParams(), "refunds");
  }

  modifyRefundLabel(refundId: number, refundLabel: string) {
    return this.get(new HttpParams().set("refundId", refundId).set("refundLabel", refundLabel), "label-update");
  }

}
