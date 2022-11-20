import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { PaymentWay } from '../../invoicing/model/PaymentWay';

@Injectable({
  providedIn: 'root'
})
export class PaymentWayService extends AppRestService<PaymentWay>{

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getPaymentWays() {
    return this.getListCached(new HttpParams(), "payment-ways");
  }

  addOrUpdatePaymentWay(paymentWay: PaymentWay) {
    this.clearListCache(new HttpParams(), "payment-ways");
    return this.addOrUpdate(new HttpParams(), "payment-way", paymentWay, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
