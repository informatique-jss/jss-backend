import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MenuItem } from '../../general/model/MenuItem';
import { AppRestService } from '../../main/services/appRest.service';
import { GtmService } from '../../main/services/gtm.service';
import { Responsable } from '../../profile/model/Responsable';
import { IQuotation } from '../../quotation/model/IQuotation';
import { NoticeTemplateService } from '../../quotation/services/notice.template.service';
import { Document } from '../model/Document';
import { Quotation } from '../model/Quotation';

@Injectable({
  providedIn: 'root'
})
export class QuotationService extends AppRestService<Quotation> {
  constructor(http: HttpClient,
    private noticeTemplateService: NoticeTemplateService,
    private gtmService: GtmService
  ) {
    super(http, "quotation");
  }

  searchQuotationsForCurrentUser(quotationStatus: string[], page: number, sorter: string, responsablesToFilter: Responsable[] | undefined) {
    let params = new HttpParams().set("page", page).set("sortBy", sorter);
    if (responsablesToFilter && responsablesToFilter.length > 0)
      params = params.set("responsableIdsToFilter", responsablesToFilter.map(r => r.id).join(","));
    return this.postList(params, "quotation/search/current", quotationStatus);
  }

  getQuotation(quotationId: number) {
    return this.get(new HttpParams().set("quotationId", quotationId), 'quotation');
  }

  cancelQuotation(quotationId: number) {
    return this.get(new HttpParams().set("quotationId", quotationId), 'quotation/cancel');
  }

  validateQuotation(quotationId: number) {
    return this.get(new HttpParams().set("quotationId", quotationId), 'quotation/validate');
  }

  getQuotationForCustomerOrder(idCustomerOrder: number) {
    return this.get(new HttpParams().set("idCustomerOrder", idCustomerOrder), 'order/quotation');
  }

  isDepositMandatory(quotationId: number): Observable<boolean> {
    return this.get(new HttpParams().set("quotationId", quotationId), 'quotation/is-deposit-mandatory') as any as Observable<boolean>;
  }

  saveQuotation(quotation: IQuotation, isValidation: boolean): Observable<number> {
    let params = new HttpParams();
    params = params.set("isValidation", isValidation);
    return this.postItem(params, 'quotation/user/save', quotation) as any as Observable<number>;
  }

  saveFinalQuotation(quotation: Quotation, isValidation: boolean) {
    let params = new HttpParams();
    params = params.set("isValidation", isValidation);
    return this.postItem(params, 'quotation/save-order', quotation);
  }

  switchResponsableForQuotation(idQuotation: number, newResponsable: Responsable) {
    return this.get(new HttpParams().set("idQuotation", idQuotation).set("newResponsable", newResponsable.id), 'quotation/switch/responsable');
  }

  completePricingOfQuotation(quotation: Quotation, isEmergency: boolean) {
    return this.postItem(new HttpParams().set("isEmergency", isEmergency), 'quotation/pricing', quotation);
  }

  setEmergencyOnQuotation(quotationId: number, isEmergency: boolean) {
    return this.get(new HttpParams().set("quotationId", quotationId).set("isEmergency", isEmergency), 'quotation/emergency');
  }

  setDocumentOnQuotation(quotationId: number, document: Document) {
    return this.postItem(new HttpParams().set("orderId", quotationId), 'quotation/document', document);
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
    quotation.lastGaClientId = this.gtmService.getGaClientId();
    localStorage.setItem('current-draft-quotation', JSON.stringify(quotation));
  }

  getCurrentDraftQuotation(): Quotation | undefined {
    if (localStorage.getItem('current-draft-quotation'))
      return JSON.parse(localStorage.getItem('current-draft-quotation')!) as Quotation;
    return undefined;
  }

  cleanStorageData() {
    this.noticeTemplateService.clearNoticeTemplateDescription();
    let allItems = localStorage as any;
    if (allItems)
      for (let key in allItems)
        if (key && key.indexOf('current-draft-quotation-id') >= 0)
          localStorage.removeItem(key);
        else if (key && key.indexOf('current-draft-quotation') >= 0)
          localStorage.removeItem(key);
        else if (key && key.indexOf('current-draft-quotation-step-route') >= 0)
          localStorage.removeItem(key);
        else if (key && key.indexOf('current-draft-order-id') >= 0)
          localStorage.removeItem(key);
        else if (key && key.indexOf('current-draft-order') >= 0)
          localStorage.removeItem(key);
  }
}
