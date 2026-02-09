import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { CustomerOrder } from '../model/CustomerOrder';
import { CustomerOrderComment } from '../model/CustomerOrderComment';
import { Quotation } from '../model/Quotation';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderCommentService extends AppRestService<CustomerOrderComment> {

  private activeOrderSource = new BehaviorSubject<number | null>(null);

  commentsResult: CustomerOrderComment[] = [];
  customerOrder: CustomerOrder = {} as CustomerOrder;

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  setWatchedOrder(order: number | null) {
    this.activeOrderSource.next(order);
  }

  getWatchedOrder() {
    return this.activeOrderSource;
  }

  getCustomerOrderCommentsForCustomer(iQuotationId: number) {
    return this.getList(new HttpParams().set("iQuotationId", iQuotationId), "customer-order-comments/from-chat");
  }

  addOrUpdateCustomerOrderComment(customerOrderComment: string, quotationId: number) {
    return this.addOrUpdate(new HttpParams().set("quotationId", quotationId), "customer-order-comment/v2", customerOrderComment as any);
  }

  markAllCommentsAsReadForIQuotation(quotationId: number) {
    return this.get(new HttpParams().set("quotationId", quotationId), "customer-order-comments/read");
  }

  getUnreadCommentsForResponsableAndIQuotation(iQuotationId: number) {
    return this.getList(new HttpParams().set("iQuotationId", iQuotationId), "customer-order-comments/unread-for-iquotation");
  }

  searchOrdersWithUnreadComments() {
    return this.getList(new HttpParams(), "customer-order-comments/orders-with-unread") as any as Observable<CustomerOrder[]>;
  }

  searchQuotationsWithUnreadComments() {
    return this.getList(new HttpParams(), "customer-order-comments/quotations-with-unread") as any as Observable<Quotation[]>;
  }
}
