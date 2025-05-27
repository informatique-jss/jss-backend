import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { IQuotation } from '../../quotation/model/IQuotation';
import { CustomerOrder } from '../model/CustomerOrder';
import { Document } from '../model/Document';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderService extends AppRestService<CustomerOrder> {
  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  searchOrdersForCurrentUser(customerOrderStatus: string[], page: number, sorter: string) {
    return this.postList(new HttpParams().set("page", page).set("sortBy", sorter), "order/search/current", customerOrderStatus);
  }

  getCustomerOrder(customerOrderId: number) {
    return this.get(new HttpParams().set("customerOrderId", customerOrderId), 'order');
  }

  getCustomerOrdersForAffaireAndCurrentUser(idAffaire: number) {
    return this.getList(new HttpParams().set("idAffaire", idAffaire), 'order/search/affaire');
  }

  downloadInvoice(order: CustomerOrder) {
    this.downloadGet(new HttpParams().set("customerOrderId", order.id + ""), "attachment/invoice/download");
  }

  getCustomerOrderForQuotation(idQuotation: number) {
    return this.get(new HttpParams().set("idQuotation", idQuotation), 'quotation/order');
  }

  saveOrder(order: IQuotation, isValidation: boolean) {
    return this.postItem(new HttpParams().set("isValidation", isValidation), 'order/user/save', order);
  }

  saveFinalOrder(order: CustomerOrder, isValidation: boolean) {
    return this.postItem(new HttpParams().set("isValidation", isValidation), 'order/save-order', order);
  }

  completePricingOfOrder(customerOrder: CustomerOrder, isEmergency: boolean) {
    return this.postItem(new HttpParams().set("isEmergency", isEmergency), 'order/pricing', customerOrder);
  }

  setEmergencyOnOrder(orderId: number, isEmergency: boolean) {
    return this.get(new HttpParams().set("orderId", orderId).set("isEmergency", isEmergency), 'order/emergency');
  }

  setDocumentOnOrder(orderId: number, document: Document) {
    return this.postItem(new HttpParams().set("orderId", orderId), 'order/document', document);
  }

  getCurrentDraftOrderId() {
    return localStorage.getItem('current-draft-order-id');
  }

  setCurrentDraftOrderId(quotationId: number) {
    localStorage.setItem('current-draft-order-id', quotationId + "");
  }

  setCurrentDraftOrder(quotation: IQuotation) {
    localStorage.setItem('current-draft-order', JSON.stringify(quotation));
  }

  getCurrentDraftOrder(): CustomerOrder | undefined {
    if (localStorage.getItem('current-draft-order'))
      return JSON.parse(localStorage.getItem('current-draft-order')!) as CustomerOrder;
    return undefined;
  }
}
