import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { PaymentDto } from '../model/PaymentDto';
import { PaymentSearch } from '../model/PaymentSearch';

@Injectable({
  providedIn: 'root'
})
export class PaymentService extends AppRestService<PaymentDto> {

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  searchPayment(paymentSearch: PaymentSearch) {
    return this.postList(new HttpParams(), "payments/search/v2", paymentSearch);
  }
}
