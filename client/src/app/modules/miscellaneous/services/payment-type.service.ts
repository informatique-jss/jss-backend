import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { PaymentType } from '../../miscellaneous/model/PaymentType';

@Injectable({
  providedIn: 'root'
})
export class PaymentTypeService extends AppRestService<PaymentType>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getPaymentTypes() {
    return this.getList(new HttpParams(), "payment-types");
  }

}
