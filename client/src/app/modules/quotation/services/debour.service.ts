import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Debour } from '../../quotation/model/Debour';
import { DebourPaymentAssociationRequest } from '../model/DebourPaymentAssociationRequest';

@Injectable({
  providedIn: 'root'
})
export class DebourService extends AppRestService<Debour>{
  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getDebours() {
    return this.getList(new HttpParams(), "debours");
  }

  addOrUpdateDebour(debour: Debour) {
    return this.addOrUpdate(new HttpParams(), "debour", debour, "Enregistré", "Erreur lors de l'enregistrement");
  }

  associateDeboursAndPayment(dialogResult: DebourPaymentAssociationRequest) {
    return this.postList(new HttpParams().set("paymentId", dialogResult.payment.id), "debour/payment/associate", dialogResult.debours);
  }

}
