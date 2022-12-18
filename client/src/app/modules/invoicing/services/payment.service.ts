import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Invoice } from '../../quotation/model/Invoice';
import { Payment } from '../model/Payment';
import { PaymentAssociate } from '../model/PaymentAssociate';

@Injectable({
  providedIn: 'root'
})
export class PaymentService extends AppRestService<Payment>{
  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getAdvisedPayment(invoice: Invoice) {
    return this.getList(new HttpParams().set("invoiceId", invoice.id), "payments/advise");
  }

  getPaymentById(paymentId: number) {
    return this.getById("payment", paymentId);
  }

  // TODO : à retirer avant la MEP !!
  addOrUpdatePayment(payment: Payment) {
    return this.addOrUpdate(new HttpParams(), "payment", payment, "Enregistré", "Erreur lors de l'enregistrement");
  }

  associatePaymentAndInvoiceAndCustomerOrder(paymentAssociate: PaymentAssociate) {
    return this.postList(new HttpParams(), "payments/associate", paymentAssociate, "Association réalisée avec succès", "Erreur lors de l'association");
  }

  setExternallyAssociated(payment: Payment) {
    return this.postList(new HttpParams(), "payments/associate/externally", payment);
  }

  unsetExternallyAssociated(payment: Payment) {
    return this.postList(new HttpParams(), "payments/unassociate/externally", payment);
  }
}
