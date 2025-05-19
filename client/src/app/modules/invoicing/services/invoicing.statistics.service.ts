import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { InvocingStatistics } from '../model/InvoicingStatistics';

@Injectable({
  providedIn: 'root'
})
export class InvocingStatisticsService extends AppRestService<InvocingStatistics> {

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getInvoicingStatistics() {
    return this.get(new HttpParams(), "invoicing/statistics");
  }

}
