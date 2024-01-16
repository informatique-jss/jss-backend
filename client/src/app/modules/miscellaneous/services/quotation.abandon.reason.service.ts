import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { QuotationAbandonReason } from '../model/QuotationAbandonReason';

@Injectable({
  providedIn: 'root'
})
export class QuotationAbandonReasonService extends AppRestService<QuotationAbandonReason>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getQuotationAbandonReasons() {
    return this.getListCached(new HttpParams(), "quotation-abandon-reasons");
  }

  addOrUpdateQuotationAbandonReason(abandonReason: QuotationAbandonReason) {
    return this.addOrUpdate(new HttpParams(), "quotation-abandon-reason/quotation", abandonReason, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
