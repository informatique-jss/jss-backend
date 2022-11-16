import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { QuotationSearch } from '../model/QuotationSearch';
import { QuotationSearchResult } from '../model/QuotationSearchResult';

@Injectable({
  providedIn: 'root'
})
export class QuotationSearchResultService extends AppRestService<QuotationSearchResult>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getQuotations(orderingSearch: QuotationSearch) {
    return this.postList(new HttpParams(), "quotation/search", orderingSearch);
  }

}
