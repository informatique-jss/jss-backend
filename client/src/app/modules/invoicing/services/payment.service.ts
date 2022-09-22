import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Payment } from '../model/Payment';
import { PaymentSearch } from '../model/PaymentSearch';

@Injectable({
  providedIn: 'root'
})
export class PaymentService extends AppRestService<Payment>{

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getPayments(paymentSearch: PaymentSearch) {
    return this.postList(new HttpParams(), "payemnts/search", paymentSearch);
  }

  // TODO : à retirer avant la MEP !!
  addOrUpdatePayment(payemnt: Payment) {
    return this.addOrUpdate(new HttpParams(), "payment", payemnt, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
