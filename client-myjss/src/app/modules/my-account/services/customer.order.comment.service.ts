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
  // public comments = this.activeOrderSource.asObservable().pipe(
  //   distinctUntilChanged((prev, curr) => prev === curr),
  //   switchMap(order => {
  //     if (!order) return of([]);
  //     return timer(0, COMMENT_POST_REFRESH_INTERVAL).pipe(
  //       switchMap(() => this.getCommentsFromChatForOrder(order))
  //     );
  //   }),
  //   shareReplay(1)
  // );
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

  addOrUpdateCustomerOrderComment(customerOrderComment: CustomerOrderComment) {
    return this.addOrUpdate(new HttpParams(), "customer-order-comment/v2", customerOrderComment);
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
