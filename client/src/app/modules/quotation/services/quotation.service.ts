import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { Affaire } from '../model/Affaire';
import { Announcement } from '../model/Announcement';
import { CustomerOrder } from '../model/CustomerOrder';
import { IQuotation } from '../model/IQuotation';
import { Quotation } from '../model/Quotation';

@Injectable({
  providedIn: 'root'
})
export class QuotationService extends AppRestService<IQuotation> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  updateQuotationStatus(quotation: IQuotation, targetStatusCode: string) {
    return this.addOrUpdate(new HttpParams().set("targetStatusCode", targetStatusCode), "quotation/status", quotation, "Statut modifié", "Erreur lors de la modification du statut");
  }

  getQuotation(idQuotation: number) {
    return this.getById("quotation", idQuotation);
  }

  getQuotationOfAnnouncement(announcement: Announcement) {
    return this.get(new HttpParams().set("idAnnouncement", announcement.id), "quotation/announcement");
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

  generateInvoicetMail(customerOrder: IQuotation) {
    return this.getList(new HttpParams().set("customerOrderId", customerOrder.id), "mail/generate/invoice", "Mail envoyé !", "Erreur lors de l'envoi du mail");
  }

  sendCustomerOrderFinalisationToCustomer(customerOrder: CustomerOrder) {
    return this.get(new HttpParams().set("customerOrderId", customerOrder.id), "mail/send/invoice", "Mail envoyé !", "Erreur lors de l'envoi du mail");
  }

  associateCustomerOrderToQuotation(customerOrderId: number, quotationId: number) {
    return this.get(new HttpParams().set("idQuotation", quotationId).set("idCustomerOrder", customerOrderId), "customer-order/associate");
  }

  searchQuotation(commercialsIds: number[], statusIds: number[]) {
    return this.getList(new HttpParams().set("commercialIds", commercialsIds.join(",")).set("statusIds", statusIds.join(',')), 'quotation/search') as Observable<Quotation[]>;
  }

  getSingleQuotation(idCustomerOrder: number) {
    return this.getById("quotation/single", idCustomerOrder);
  }

  getQuotationByAffaire(affaire: Affaire) {
    return this.getList(new HttpParams().set("idAffaire", affaire.id), 'quotations/affaire');
  }
}
