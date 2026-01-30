import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, distinctUntilChanged, of, shareReplay, switchMap, timer } from 'rxjs';
import { COMMENT_POST_REFRESH_INTERVAL } from '../../main/model/Constant';
import { AppRestService } from '../../main/services/appRest.service';
import { CustomerOrderComment } from '../model/CustomerOrderComment';
import { CustomerOrderDto } from '../model/CustomerOrderDto';


@Injectable({
  providedIn: 'root'
})
export class IQuotationCommentService extends AppRestService<CustomerOrderComment> {

  private activeOrderSource = new BehaviorSubject<number | null>(null);

  public comments = this.activeOrderSource.asObservable().pipe(
    distinctUntilChanged((prev, curr) => prev === curr),
    switchMap(orderId => {
      if (!orderId) return of([]);
      return timer(0, COMMENT_POST_REFRESH_INTERVAL).pipe(
        switchMap(() => this.getCommentsFromChatForOrder(orderId))
      );
    }),
    shareReplay(1)
  );

  commentsResult: CustomerOrderComment[] = [];

  customerOrder: CustomerOrderDto = {} as CustomerOrderDto;

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  setWatchedOrder(order: number | null) {
    this.activeOrderSource.next(order);
  }

  getCommentsFromChatForOrder(iQuotationId: number) {
    return this.getList(new HttpParams().set("customerOrderId", iQuotationId), "customer-order-comments/from-chat");
  }

  getCustomerOrderCommentForOrder(iQuotationId: number) {
    return this.getList(new HttpParams().set("customerOrderId", iQuotationId), "customer-order-comment/order");
  }

  addOrUpdateCustomerOrderComment(customerOrderComment: CustomerOrderComment) {
    return this.addOrUpdate(new HttpParams(), "customer-order-comment/v2", customerOrderComment, "Enregistré", "Erreur lors de l'enregistrement");
  }
}