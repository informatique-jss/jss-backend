import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { QuotationReporting } from '../model/QuotationReporting';

@Injectable({
  providedIn: 'root'
})
export class QuotationReportingService extends AppRestService<QuotationReporting>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getQuotationReporting(columns: string[]) {
    return this.getList(new HttpParams().set("columns", columns.join(",")), "quotation");
  }

}
