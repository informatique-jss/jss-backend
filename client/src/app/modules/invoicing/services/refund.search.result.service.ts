import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { RefundSearch } from '../model/RefundSearch';
import { RefundSearchResult } from '../model/RefundSearchResult';

@Injectable({
  providedIn: 'root'
})
export class RefundSearchResultService extends AppRestService<RefundSearchResult>{
  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getRefunds(refundSearch: RefundSearch) {
    return this.postList(new HttpParams(), "refunds/search", refundSearch);
  }

  exportRefunds(refundSearch: RefundSearch) {
    return this.downloadPost("refunds/export", refundSearch as any);
  }
}
