import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { CustomerOrderComment } from '../../quotation/model/CustomerOrderComment';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderCommentService extends AppRestService<CustomerOrderComment> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getCustomerOrderComments() {
    return this.getList(new HttpParams(), "customer-order-comments");
  }

  getCustomerOrderCommentForOrder(customerOrderId: number) {
    return this.getList(new HttpParams().set("customerOrderId", customerOrderId), "customer-order-comment/order");
  }

  getCustomerOrderCommentForQuotation(quotationId: number) {
    return this.getList(new HttpParams().set("quotationId", quotationId), "customer-order-comment/quotation");
  }

  getCustomerOrderCommentForProvision(provisionId: number) {
    return this.getList(new HttpParams().set("provisionId", provisionId), "customer-order-comment/provision");
  }

  addOrUpdateCustomerOrderComment(customerOrderComment: CustomerOrderComment) {
    return this.addOrUpdate(new HttpParams(), "customer-order-comment", customerOrderComment, "Enregistré", "Erreur lors de l'enregistrement");
  }

  toggleCustomerOrderCommentIsRead(customerOrderComment: CustomerOrderComment) {
    return this.get(new HttpParams().set('customerOrderCommentId', customerOrderComment.id), "customer-order-comment/toggle-read", (customerOrderComment.isRead) ? "Commentaire marqué comme non-lu" : "Commentaire marqué comme lu", "Erreur lors de l'enregistrement");
  }

}
