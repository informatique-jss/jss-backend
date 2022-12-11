import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Deposit } from '../../invoicing/model/Deposit';
import { PaymentAssociate } from '../model/PaymentAssociate';

@Injectable({
  providedIn: 'root'
})
export class DepositService extends AppRestService<Deposit>{

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  associateDepositAndInvoiceAndCustomerOrder(paymentAssociate: PaymentAssociate) {
    return this.postList(new HttpParams(), "deposits/associate", paymentAssociate, "Association réalisée avec succès", "Erreur lors de l'association");
  }
}
