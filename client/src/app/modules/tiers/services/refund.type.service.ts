import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
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

  addOrUpdateRefundType(refundType: RefundType) {
    return this.addOrUpdate(new HttpParams(), "refund-type", refundType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
