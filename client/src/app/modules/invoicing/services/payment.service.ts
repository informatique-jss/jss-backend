import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { AppRestService } from 'src/app/services/appRest.service';
import { CustomerOrder } from '../../quotation/model/CustomerOrder';
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

  getAdvisedPaymentForCustomerOrder(customerOrder: CustomerOrder) {
    return this.getList(new HttpParams().set("customerOrderId", customerOrder.id), "payments/advise/order");
  }

  getPaymentById(paymentId: number) {
    return this.getById("payment", paymentId);
  }

  associatePaymentAndInvoiceAndCustomerOrder(paymentAssociate: PaymentAssociate) {
    return this.postList(new HttpParams(), "payments/associate", paymentAssociate, "Association réalisée avec succès", "Erreur lors de l'association");
  }

  addCashPaymentForInvoice(payment: Payment, invoice: Invoice) {
    return this.postItem(new HttpParams().set("idInvoice", invoice.id), "payment/cash/add/invoice", payment);
  }

  addCashPaymentForCustomerOrder(payment: Payment, customerOrder: CustomerOrder) {
    return this.postItem(new HttpParams().set("idCustomerOrder", customerOrder.id), "payment/cash/add/customer-order", payment);
  }

  addCheckPayment(payment: Payment) {
    return this.postItem(new HttpParams(), "payment/check/add", payment);
  }

  refundPayment(payment: Payment, tiers: IndexEntity, affaire: IndexEntity) {
    if (affaire)
      return this.get(new HttpParams().set("paymentId", payment.id).set("tiersId", tiers.entityId).set("affaireId", affaire.entityId), "refund/payment", "Paiement remboursé", "Erreur lors du remboursement du paiement");
    else
      return this.get(new HttpParams().set("paymentId", payment.id).set("tiersId", tiers.entityId).set("affaireId", 1), "refund/payment", "Paiement remboursé", "Erreur lors du remboursement du paiement");
  }
}
