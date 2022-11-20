import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { PaymentType } from '../../miscellaneous/model/PaymentType';

@Injectable({
  providedIn: 'root'
})
export class PaymentTypeService extends AppRestService<PaymentType>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getPaymentTypes() {
    return this.getListCached(new HttpParams(), "payment-types");
  }

  addOrUpdatePaymentType(paymentType: PaymentType) {
    this.clearListCache(new HttpParams(), "payment-types");
    return this.addOrUpdate(new HttpParams(), "payment-type", paymentType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
