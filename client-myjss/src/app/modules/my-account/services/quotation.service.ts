import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { MenuItem } from '../../general/model/MenuItem';
import { IQuotation } from '../../quotation/model/IQuotation';
import { Quotation } from '../model/Quotation';

@Injectable({
  providedIn: 'root'
})
export class QuotationService extends AppRestService<Quotation> {
  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  searchQuotationsForCurrentUser(quotationStatus: string[], page: number, sorter: string) {
    return this.postList(new HttpParams().set("page", page).set("sortBy", sorter), "quotation/search/current", quotationStatus);
  }

  getQuotation(quotationId: number) {
    return this.get(new HttpParams().set("quotationId", quotationId), 'quotation');
  }

  getQuotationForCustomerOrder(idCustomerOrder: number) {
    return this.get(new HttpParams().set("idCustomerOrder", idCustomerOrder), 'order/quotation');
  }

  saveQuotation(quotation: IQuotation) {
    return this.postItem(new HttpParams(), 'quotation/user/save', quotation);
  }

  getCurrentDraftQuotationId() {
    return localStorage.getItem('current-draft-quotation-id');
  }

  setCurrentDraftQuotationId(quotationId: number) {
    localStorage.setItem('current-draft-quotation-id', quotationId + "");
  }

  getCurrentDraftQuotationStep() {
    return localStorage.getItem('current-draft-quotation-step-route');
  }

  setCurrentDraftQuotationStep(item: MenuItem) {
    localStorage.setItem('current-draft-quotation-step-route', item.route);
  }

  setCurrentDraftQuotation(quotation: IQuotation) {
    localStorage.setItem('current-draft-quotation', JSON.stringify(quotation));
  }

  getCurrentDraftQuotation(): Quotation | undefined {
    if (localStorage.getItem('current-draft-quotation'))
      return JSON.parse(localStorage.getItem('current-draft-quotation')!) as Quotation;
    return undefined;
  }

}
