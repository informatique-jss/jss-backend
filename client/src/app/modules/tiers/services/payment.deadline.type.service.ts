import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { PaymentDeadlineType } from '../../tiers/model/PaymentDeadlineType';

@Injectable({
  providedIn: 'root'
})
export class PaymentDeadlineTypeService extends AppRestService<PaymentDeadlineType>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getPaymentDeadlineTypes() {
    return this.getList(new HttpParams(), "payment-deadline-types");
  }

}
