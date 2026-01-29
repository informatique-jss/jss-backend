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
export class CustomerOrderCommentService extends AppRestService<CustomerOrderComment> {

  private activeOrderSource = new BehaviorSubject<CustomerOrderDto | null>(null);

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

  customerOrder: CustomerOrderDto = {} as CustomerOrderDto;

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  setWatchedOrder(order: CustomerOrderDto | null) {
    this.activeOrderSource.next(order);
  }

  getCommentsFromChatForOrder(customerOrder: CustomerOrderDto) {
    return this.getList(new HttpParams().set("customerOrderId", customerOrder.id), "customer-order-comments/from-chat");
  }

  getCustomerOrderCommentForOrder(customerOrderId: number) {
    return this.getList(new HttpParams().set("customerOrderId", customerOrderId), "customer-order-comment/order");
  }

  addOrUpdateCustomerOrderComment(customerOrderComment: CustomerOrderComment) {
    return this.addOrUpdate(new HttpParams(), "customer-order-comment/v2", customerOrderComment, "Enregistré", "Erreur lors de l'enregistrement");
  }
}