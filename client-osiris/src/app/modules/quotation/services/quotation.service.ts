import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { QuotationDto } from '../model/QuotationDto';
import { QuotationSearch } from '../model/QuotationSearch';

@Injectable({
  providedIn: 'root'
})
export class QuotationService extends AppRestService<QuotationDto> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  searchQuotation(quotationSearch: QuotationSearch) {
    return this.postList(new HttpParams(), "quotation/search/v2", quotationSearch);
  }

  getTiersByIQuotation(iQuotationId: number) {
    return this.get(new HttpParams().set("iQuotationId", iQuotationId), "quotation/tiers") as any as Observable<number>;
  }

  getQuotation(quotationId: number) {
    return this.getById("quotation", quotationId)
  }

  getIsQuotation(iQuotationId: number) {
    return this.get(new HttpParams().set("iQuotationId", iQuotationId), "quotation/is-quotation");
  }
}