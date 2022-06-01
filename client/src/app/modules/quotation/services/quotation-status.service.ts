import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { QuotationStatus } from '../../quotation/model/QuotationStatus';

@Injectable({
  providedIn: 'root'
})
export class QuotationStatusService extends AppRestService<QuotationStatus>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getQuotationStatus() {
    return this.getList(new HttpParams(), "quotation-status");
  }

}
