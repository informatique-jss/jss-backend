import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { RefundType } from '../../tiers/model/RefundType';

@Injectable({
  providedIn: 'root'
})
export class RefundTypeService extends AppRestService<RefundType>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getRefundTypes() {
    return this.getList(new HttpParams(), "refund-types");
  }

}
