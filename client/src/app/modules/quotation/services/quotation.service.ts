import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { IQuotation } from '../model/IQuotation';
import { OrderingSearch } from '../model/OrderingSearch';

@Injectable({
  providedIn: 'root'
})
export class QuotationService extends AppRestService<IQuotation>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  updateQuotationStatus(quotation: IQuotation, targetStatusCode: string) {
    return this.addOrUpdate(new HttpParams().set("targetStatusCode", targetStatusCode), "quotation/status", quotation, "Devis enregistré", "Erreur lors de l'enregistrement du devis");
  }

  getQuotation(idQuotation: number) {
    return this.getById("quotation", idQuotation);
  }

  getOrders(orderingSearch: OrderingSearch) {
    return this.postList(new HttpParams(), "order/search", orderingSearch);
  }

  getQuotations(orderingSearch: OrderingSearch) {
    return this.postList(new HttpParams(), "quotation/search", orderingSearch);
  }

  addOrUpdateQuotation(quotation: IQuotation) {
    return this.addOrUpdate(new HttpParams(), "quotation", quotation, "Devis enregistré", "Erreur lors de l'enregistrement du devis");
  }

  getInvoiceItemsForQuotation(quotation: IQuotation) {
    return this.addOrUpdate(new HttpParams(), "invoice-item/generate", quotation);
  }
}
