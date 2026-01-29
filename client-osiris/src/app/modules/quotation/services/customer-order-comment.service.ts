import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, retry, share, Subject, switchMap, takeUntil, tap, timer } from 'rxjs';
import { COMMENT_POST_REFRESH_INTERVAL } from '../../main/model/Constant';
import { AppRestService } from '../../main/services/appRest.service';
import { CustomerOrderComment } from '../model/CustomerOrderComment';
import { CustomerOrderDto } from '../model/CustomerOrderDto';


@Injectable({
  providedIn: 'root'
})
export class CustomerOrderCommentService extends AppRestService<CustomerOrderComment> {

  private activeOrderSource = new BehaviorSubject<CustomerOrderDto | null>(null);
  activeOrderSourceObservable = this.activeOrderSource.asObservable();

  comments: Observable<CustomerOrderComment[]> | undefined;
  commentsResult: CustomerOrderComment[] = [];

  customerOrder: CustomerOrderDto = {} as CustomerOrderDto;

  private stopPolling = new Subject();

  constructor(http: HttpClient) {
    super(http, "quotation");

    // this.comments = this.activeOrderSource.asObservable().pipe(
    //   switchMap(order => {
    //     if (!order || !order.id) {
    //       return of([]); // If no order (e.g.: Quote page), return an empty list
    //     }
    //     // Start polling (every 1000ms)
    //     return timer(0, 1000).pipe(
    //       switchMap(() => this.getCommentsFromChatForOrder(order)),
    //       share()
    //     );
    //   })
    // );

    this.activeOrderSourceObservable.subscribe(res => {
      if (res) {
        this.comments = timer(1, COMMENT_POST_REFRESH_INTERVAL).pipe(
          switchMap(() => this.getCommentsFromChatForOrder(res)),
          retry(),
          tap((value) => {
            this.commentsResult = value;
            if (!this.commentsResult)
              this.commentsResult = [];
          }),
          share(),
          takeUntil(this.stopPolling)
        );
        this.comments.subscribe();
      }
    });
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
