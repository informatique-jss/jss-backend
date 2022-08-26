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
    return this.getList(new HttpParams(), "payment-types");
  }

  addOrUpdatePaymentType(paymentType: PaymentType) {
    return this.addOrUpdate(new HttpParams(), "payment-type", paymentType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
