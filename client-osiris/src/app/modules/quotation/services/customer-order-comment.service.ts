import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, share, switchMap, timer } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { CustomerOrderCommentDto } from '../model/CustomerOrderCommentDto';
import { CustomerOrderDto } from '../model/CustomerOrderDto';


@Injectable({
  providedIn: 'root'
})
export class CustomerOrderCommentService extends AppRestService<CustomerOrderCommentDto> {

  private activeOrderSource = new BehaviorSubject<CustomerOrderDto | null>(null);
  comments: Observable<CustomerOrderCommentDto[]> | undefined;
  commentsResult: CustomerOrderCommentDto[] = [];
  customerOrder: CustomerOrderDto = {} as CustomerOrderDto;
  constructor(http: HttpClient) {
    super(http, "quotation");

    this.comments = this.activeOrderSource.asObservable().pipe(
      switchMap(order => {
        if (!order || !order.id) {
          return of([]); // Si pas de commande (ex: page Devis), on renvoie une liste vide
        }
        // Lancement du polling (toutes les 1000ms)
        return timer(0, 1000).pipe(
          switchMap(() => this.getCommentsFromTchatForOrder(order)),
          share()
        );
      })
    );
  }

  setWatchedOrder(order: CustomerOrderDto | null) {
    this.activeOrderSource.next(order);
  }

  getCommentsFromTchatForOrder(customerOrder: CustomerOrderDto) {
    return this.getList(new HttpParams().set("customerOrderId", customerOrder.id), "customer-order-comments/from-tchat");
  }

  getCustomerOrderCommentForOrder(customerOrderId: number) {
    return this.getList(new HttpParams().set("customerOrderId", customerOrderId), "customer-order-comment/order");
  }

  addOrUpdateCustomerOrderComment(customerOrderCommentDto: CustomerOrderCommentDto) {
    return this.addOrUpdate(new HttpParams(), "customer-order-comment/v2", customerOrderCommentDto, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
