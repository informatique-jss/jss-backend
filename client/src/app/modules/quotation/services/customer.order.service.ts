import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { Voucher } from '../../crm/model/Voucher';
import { InvoicingBlockage } from '../../invoicing/model/InvoicingBlockage';
import { CompetentAuthority } from '../../miscellaneous/model/CompetentAuthority';
import { Employee } from '../../profile/model/Employee';
import { Announcement } from '../model/Announcement';
import { CustomerOrder } from '../model/CustomerOrder';
import { IQuotation } from '../model/IQuotation';
import { Invoice } from '../model/Invoice';

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

  offerCustomerOrder(customerOrder: CustomerOrder) {
    return this.get(new HttpParams().set("customerOrderId", customerOrder.id), "customer-order/offer");
  }

  generateCreditNoteForCustomerOrderInvoice(invoice: Invoice, customerOrder: CustomerOrder) {
    return this.get(new HttpParams().set("invoiceId", invoice.id).set("customerOrderId", customerOrder.id), "customer-order/credit-note");
  }

  reinitInvoicing(quotation: CustomerOrder) {
    return this.get(new HttpParams().set("customerOrderId", quotation.id), "customer-order/invoicing/reinit", "Facturation réinitialisée");
  }

  assignInvoicingEmployee(customerOrderId: number, employee: Employee) {
    let params = new HttpParams().set("customerOrderId", customerOrderId);
    if (employee)
      params = params.set("employeeId", employee.id);
    return this.get(params, "customer-order/assign/invoicing", employee ? ("Facturation assignée à " + employee.firstname + " " + employee.lastname) : '');
  }

  modifyInvoicingBlockage(customerOrderId: number, invoicingBlockage: InvoicingBlockage | undefined) {
    let params = new HttpParams().set("customerOrderId", customerOrderId);
    if (invoicingBlockage)
      params = params.set("invoicingBlockageId", invoicingBlockage.id);
    return this.get(params, "customer-order/change/invoicing-blockage", invoicingBlockage ? ("Blocage de la facturation pour cause de " + invoicingBlockage.label) : '');
  }

  searchCustomerOrder(commercialsIds: number[], statusIds: number[]) {
    return this.getList(new HttpParams().set("commercialIds", commercialsIds.join(",")).set("statusIds", statusIds.join(',')), 'customer-order/search') as Observable<CustomerOrder[]>;
  }

  searchCustomerOrderForInvoicing(employeeIds: number[]) {
    let params = new HttpParams();
    if (employeeIds)
      params = params.set("employeeIds", employeeIds.join(","));
    return this.getList(params, 'customer-order/search/invoicing') as Observable<CustomerOrder[]>;
  }

  getSingleCustomerOrder(idCustomerOrder: number) {
    return this.getById("customer-order/single", idCustomerOrder);
  }

  assignNewCustomerOrderToBilled() {
    return this.get(new HttpParams(), "customer-order/assign/invoicing/auto");
  }

  getCustomerOrdersByVoucher(voucher: Voucher) {
    return this.getList(new HttpParams().set("idVoucher", voucher.id), 'customer-orders/voucher') as Observable<CustomerOrder[]>;
  }

  getOrdersToAssignForFond(teamEmployee: Employee, onlyCurrentUser: boolean) {
    return this.getList(new HttpParams().set("idTeamEmployee", teamEmployee.id).set("onlyCurrentUser", onlyCurrentUser), "assign/fond/order");
  }

  createOrderFromSuggestedQuotation(quotationId: number) {
    return this.get(new HttpParams().set("idQuotation", quotationId), "customer-order/suggested-quotation");
  }
}
