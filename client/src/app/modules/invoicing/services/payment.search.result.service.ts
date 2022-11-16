import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { PaymentSearch } from '../model/PaymentSearch';
import { PaymentSearchResult } from '../model/PaymentSearchResult';

@Injectable({
  providedIn: 'root'
})
export class PaymentSearchResultService extends AppRestService<PaymentSearchResult>{
  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getPayments(paymentSearch: PaymentSearch) {
    return this.postList(new HttpParams(), "payments/search", paymentSearch);
  }
}
