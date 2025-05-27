import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { BillingLabelType } from '../../my-account/model/BillingLabelType';

@Injectable({
  providedIn: 'root'
})
export class BillingLabelTypeService extends AppRestService<BillingLabelType> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getBillingLabelTypes() {
    return this.getListCached(new HttpParams(), "billing-label-types");
  }
}
