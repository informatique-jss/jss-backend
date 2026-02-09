import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { CustomerOrderComment } from '../model/CustomerOrderComment';


@Injectable({
  providedIn: 'root'
})
export class IQuotationCommentService extends AppRestService<CustomerOrderComment> {

  private IQuotationChangeSubject = new BehaviorSubject<boolean>(false);
  private IQuotationChangeObservable = this.IQuotationChangeSubject.asObservable();

  watchedIQuotations: number[] = [];

  private isIQuotationChatExpandedSubject: BehaviorSubject<Map<number, boolean>> = new BehaviorSubject<Map<number, boolean>>(new Map<number, boolean>());
  isIQuotationChatExpandedObservable = this.isIQuotationChatExpandedSubject.asObservable();

  isIQuotationChatExpanded: number[] = [];

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getActiveOrderSourceObservable() {
    return this.IQuotationChangeObservable;
  }

  openChatForIQuotation(iQuotationId: number) {
    if (iQuotationId) {
      this.addToWatchedIQuotations(iQuotationId);
      this.toggleIsExpanded(iQuotationId);
    }
  }

  emptyWatchedIQuotations() {
    this.IQuotationChangeSubject.next(false);
    return localStorage.setItem('watched-iquotations-chat', "");
  }

  getWatchedIQuotations(): number[] {
    if (!this.watchedIQuotations || this.watchedIQuotations.length == 0)
      this.watchedIQuotations = localStorage.getItem('watched-iquotations-chat') ? JSON.parse(localStorage.getItem('watched-iquotations-chat')!) : [];
    return this.watchedIQuotations;
  }

  addToWatchedIQuotations(iQuotationId: number) {
    if (!iQuotationId)
      return;
    if (this.watchedIQuotations.indexOf(iQuotationId) < 0) {
      this.watchedIQuotations.push(iQuotationId);
      localStorage.setItem('watched-iquotations-chat', JSON.stringify(this.watchedIQuotations));
      this.IQuotationChangeSubject.next(true);
    }
  }

  removeFromWatchedIQuotations(iQuotationId: number) {
    if (this.watchedIQuotations && this.watchedIQuotations.indexOf(iQuotationId) >= 0) {
      this.watchedIQuotations = this.watchedIQuotations.filter(id => id !== iQuotationId);
      localStorage.setItem('watched-iquotations-chat', JSON.stringify(this.watchedIQuotations));
      this.IQuotationChangeSubject.next(false);
      if (this.getIsExpanded(iQuotationId))
        this.toggleIsExpanded(iQuotationId);
    }
  }

  toggleIsExpanded(iQuotationId: number): boolean {
    if (this.isIQuotationChatExpanded.includes(iQuotationId))
      this.isIQuotationChatExpanded.splice(this.isIQuotationChatExpanded.indexOf(iQuotationId), 1);
    else
      this.isIQuotationChatExpanded.push(iQuotationId);

    this.setIQuotationChatExpandedLocalStorage(this.isIQuotationChatExpanded);
    return this.isIQuotationChatExpanded.indexOf(iQuotationId) >= 0 ? true : false;
  }

  getIsExpanded(iQuotationId: number): boolean {
    return this.isIQuotationChatExpanded.indexOf(iQuotationId) >= 0 ? true : false;
  }

  setIQuotationChatExpandedLocalStorage(isIQuotationChatExpanded: number[]) {
    localStorage.setItem('iquotation-chat-expanded-map', JSON.stringify(isIQuotationChatExpanded, this.replacer));
  }

  // Map serialization utils replacer
  replacer(key: any, value: any) {
    if (value instanceof Map) {
      return {
        dataType: 'Map',
        value: Array.from(value.entries()),
      };
    } else {
      return value;
    }
  }

  // ======== REST services ============
  getCommentsFromChatForIQuotations(iQuotationIds: number[]) {
    return this.postList(new HttpParams(), "customer-order-comments/from-chat", iQuotationIds);
  }

  getUnreadCommentsForEmployee() {
    return this.getList(new HttpParams(), "customer-order-comments/unread");
  }

  addOrUpdateCustomerOrderComment(customerOrderComment: string, quotationId: number) {
    return this.addOrUpdate(new HttpParams().set("quotationId", quotationId), "customer-order-comment/v2", customerOrderComment as any);
  }

  markAllCommentsAsReadForIQuotation(quotationId: number) {
    return this.get(new HttpParams().set("quotationId", quotationId), "customer-order-comments/read");
  }
}
