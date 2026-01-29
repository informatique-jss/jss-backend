import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, distinctUntilChanged, of, shareReplay, switchMap, timer } from 'rxjs';
import { COMMENT_POST_REFRESH_INTERVAL } from '../../../libs/Constants';
import { AppRestService } from '../../main/services/appRest.service';
import { CustomerOrder } from '../model/CustomerOrder';
import { CustomerOrderComment } from '../model/CustomerOrderComment';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderCommentService extends AppRestService<CustomerOrderComment> {

  private activeOrderSource = new BehaviorSubject<CustomerOrder | null>(null);
  public comments = this.activeOrderSource.asObservable().pipe(
    distinctUntilChanged((prev, curr) => prev?.id === curr?.id),
    switchMap(order => {
      if (!order || !order.id) return of([]);
      return timer(0, COMMENT_POST_REFRESH_INTERVAL).pipe(
        switchMap(() => this.getCommentsFromChatForOrder(order))
      );
    }),
    shareReplay(1)
  );
  commentsResult: CustomerOrderComment[] = [];
  customerOrder: CustomerOrder = {} as CustomerOrder;

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  setWatchedOrder(order: CustomerOrder | null) {
    this.activeOrderSource.next(order);
  }

  getCommentsFromChatForOrder(customerOrder: CustomerOrder) {
    return this.getList(new HttpParams().set("customerOrderId", customerOrder.id), "customer-order-comments/from-chat");
  }

  getCustomerOrderCommentsForCustomer(idCustomerOrder: number) {
    return this.getList(new HttpParams().set("idCustomerOrder", idCustomerOrder), "customer-order-comments/customer");
  }

  addOrUpdateCustomerOrderComment(customerOrderComment: CustomerOrderComment) {
    return this.addOrUpdate(new HttpParams(), "customer-order-comment", customerOrderComment);
  }
}
