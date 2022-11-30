import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { IQuotation } from '../model/IQuotation';

@Injectable({
  providedIn: 'root'
})
export class QuotationService extends AppRestService<IQuotation>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  updateQuotationStatus(quotation: IQuotation, targetStatusCode: string) {
    return this.addOrUpdate(new HttpParams().set("targetStatusCode", targetStatusCode), "quotation/status", quotation, "Statut modifié", "Erreur lors de la modification du statut");
  }

  getQuotation(idQuotation: number) {
    return this.getById("quotation", idQuotation);
  }

  addOrUpdateQuotation(quotation: IQuotation) {
    return this.addOrUpdate(new HttpParams(), "quotation", quotation, "Devis enregistré", "Erreur lors de l'enregistrement du devis");
  }

  getInvoiceItemsForQuotation(quotation: IQuotation) {
    return this.addOrUpdate(new HttpParams(), "invoice-item/generate", quotation);
  }

  generateQuotationMail(quotation: IQuotation) {
    return this.getList(new HttpParams().set("quotationId", quotation.id), "mail/generate/quotation", "Mail envoyé !", "Erreur lors de l'envoi du mail");
  }

  generateCustomerOrderCreationConfirmationToCustomer(customerOrder: IQuotation) {
    return this.getList(new HttpParams().set("customerOrderId", customerOrder.id), "mail/generate/customer-order-confirmation", "Mail envoyé !", "Erreur lors de l'envoi du mail");
  }

  generateCustomerOrderDepositConfirmationToCustomer(customerOrder: IQuotation) {
    return this.getList(new HttpParams().set("customerOrderId", customerOrder.id), "mail/generate/customer-order-deposit-confirmation", "Mail envoyé !", "Erreur lors de l'envoi du mail");
  }

  generateInvoicetMail(customerOrder: IQuotation) {
    return this.getList(new HttpParams().set("customerOrderId", customerOrder.id), "mail/generate/invoice", "Mail envoyé !", "Erreur lors de l'envoi du mail");
  }
}
