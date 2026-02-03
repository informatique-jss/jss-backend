import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { GoogleAnalyticsService } from '../../main/services/googleAnalytics.service';
import { Responsable } from '../../profile/model/Responsable';
import { IQuotation } from '../../quotation/model/IQuotation';
import { CustomerOrder } from '../model/CustomerOrder';
import { Document } from '../model/Document';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderService extends AppRestService<CustomerOrder> {
  constructor(http: HttpClient,
    private googleAnalyticsService: GoogleAnalyticsService
  ) {
    super(http, "quotation");
  }

  searchOrdersForCurrentUser(customerOrderStatus: string[], requiringAttention: boolean, page: number, sorter: string, responsablesToFilter: Responsable[] | undefined) {

    let params = new HttpParams().set("page", page).set("sortBy", sorter).set("requiringAttention", requiringAttention);
    if (responsablesToFilter && responsablesToFilter.length > 0)
      params = params.set("responsableIdsToFilter", responsablesToFilter.map(r => r.id).join(","));
    return this.postList(params, "order/search/current", customerOrderStatus);
  }

  getCustomerOrder(customerOrderId: number) {
    return this.get(new HttpParams().set("customerOrderId", customerOrderId), 'order');
  }

  cancelCustomerOrder(customerOrderId: number) {
    return this.get(new HttpParams().set("customerOrderId", customerOrderId), 'order/cancel');
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

  saveOrder(order: IQuotation, isValidation: boolean): Observable<number> {
    let params = new HttpParams();
    params = params.set("isValidation", isValidation);
    return this.postItem(params, 'order/user/save', order) as any as Observable<number>;
  }

  saveFinalOrder(order: CustomerOrder, isValidation: boolean) {
    let params = new HttpParams();
    params = params.set("isValidation", isValidation);
    return this.postItem(params, 'order/save-order', order);
  }

  switchResponsableForOrder(idOrder: number, newResponsable: Responsable) {
    return this.get(new HttpParams().set("idOrder", idOrder).set("newResponsable", newResponsable.id), 'order/switch/responsable');
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

  getCustomerOrderForSubscription(subscriptionType: string, isPriceReductionForSubscription: boolean, idArticle: number | undefined) {
    let params = new HttpParams();
    if (idArticle)
      params = params.set("idArticle", idArticle);
    params = params.set("subscriptionType", subscriptionType);
    params = params.set("isPriceReductionForSubscription", isPriceReductionForSubscription);

    return this.get(params, 'order/subscription');
  }

  getCurrentDraftOrderId() {
    return localStorage.getItem('current-draft-order-id');
  }

  setCurrentDraftOrderId(quotationId: number) {
    localStorage.setItem('current-draft-order-id', quotationId + "");
  }

  setCurrentDraftOrder(quotation: IQuotation) {
    quotation.lastGaClientId = this.googleAnalyticsService.getGaClientId();
    localStorage.setItem('current-draft-order', JSON.stringify(quotation));
  }

  getCurrentDraftOrder(): CustomerOrder | undefined {
    if (localStorage.getItem('current-draft-order'))
      return JSON.parse(localStorage.getItem('current-draft-order')!) as CustomerOrder;
    return undefined;
  }

  getCardPaymentLinkForPaymentInvoices(customerOrderIds: number[]) {
    return this.postItem(new HttpParams(), "payment/cb/invoice", customerOrderIds) as any as Observable<any>;
  }
}
