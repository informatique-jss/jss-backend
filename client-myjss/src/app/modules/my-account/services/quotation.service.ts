import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
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
}
