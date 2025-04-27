import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { CompetentAuthority } from '../../miscellaneous/model/CompetentAuthority';
import { Employee } from '../../profile/model/Employee';
import { Announcement } from '../model/Announcement';
import { CustomerOrder } from '../model/CustomerOrder';
import { IQuotation } from '../model/IQuotation';
import { Invoice } from '../model/Invoice';
import { Quotation } from '../model/Quotation';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderService extends AppRestService<IQuotation> {
  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  updateCustomerStatus(customerOrder: IQuotation, targetStatusCode: string) {
    return this.addOrUpdate(new HttpParams().set("targetStatusCode", targetStatusCode), "customer-order/status", customerOrder, "Commande enregistrée", "Erreur lors de la mise à jour du statut");
  }

  getCustomerOrder(idCustomerOrder: number) {
    return this.getById("customer-order", idCustomerOrder);
  }

  getCustomerOrderForInvoice(invoice: Invoice) {
    return this.get(new HttpParams().set("idInvoice", invoice.id), "invoice/customer-order");
  }

  addOrUpdateCustomerOrder(customerOrder: IQuotation) {
    return this.addOrUpdate(new HttpParams(), "customer-order", customerOrder, "Commande enregistrée", "Erreur lors de l'enregistrement de la commande");
  }

  getCustomerOrderOfAnnouncement(announcement: Announcement) {
    return this.get(new HttpParams().set("idAnnouncement", announcement.id), "customer-order/announcement");
  }

  generateMailingLabelDownload(customerOrders: string[], printLabel: boolean, competentAuthority: CompetentAuthority | undefined, printLetters: boolean, printRegisteredLetter: boolean) {
    let params = new HttpParams().set("customerOrders", customerOrders.join(",")).set("printLabel", printLabel).set("printLetters", printLetters).set("printRegisteredLetter", printRegisteredLetter);
    if (competentAuthority)
      params = params.set("competentAuthorityId", competentAuthority.id + "");
    return this.downloadGet(params, "customer-order/print/label");
  }

  generateMailingLabel(customerOrders: string[], printLabel: boolean, competentAuthority: CompetentAuthority | undefined, printLetters: boolean, printRegisteredLetter: boolean) {
    let params = new HttpParams().set("customerOrders", customerOrders.join(",")).set("printLabel", printLabel).set("printLetters", printLetters).set("printRegisteredLetter", printRegisteredLetter);
    if (competentAuthority)
      params = params.set("competentAuthorityId", competentAuthority.id + "");
    return this.get(params, "customer-order/print/label");
  }

  updateAssignedToForCustomerOrder(customerOrder: CustomerOrder, employee: Employee) {
    return this.get(new HttpParams().set("customerOrderId", customerOrder.id).set("employeeId", employee.id), "customer-order/assign");
  }

  updateAssignedToForQuotation(quotation: Quotation, employee: Employee) {
    return this.get(new HttpParams().set("quotationId", quotation.id).set("employeeId", employee.id), "quotation/assign");
  }

  offerCustomerOrder(customerOrder: CustomerOrder) {
    return this.get(new HttpParams().set("customerOrderId", customerOrder.id), "customer-order/offer");
  }

  generateCreditNoteForCustomerOrderInvoice(invoice: Invoice, customerOrder: CustomerOrder) {
    return this.get(new HttpParams().set("invoiceId", invoice.id).set("customerOrderId", customerOrder.id), "customer-order/credit-note");
  }

  reinitInvoicing(quotation: CustomerOrder) {
    return this.get(new HttpParams().set("customerOrderId", quotation.id), "customer-order/invoicing/reinit", "Facturation réinitialisée");
  }

  searchCustomerOrder(commercialsIds: number[], statusIds: number[]) {
    return this.getList(new HttpParams().set("commercialIds", commercialsIds.join(",")).set("statusIds", statusIds.join(',')), 'customer-order/search') as Observable<CustomerOrder[]>;
  }

  getSingleCustomerOrder(idCustomerOrder: number) {
    return this.getById("customer-order/single", idCustomerOrder);
  }


}
