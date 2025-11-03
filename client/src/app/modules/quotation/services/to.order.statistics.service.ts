import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { ToOrderStatistics } from '../model/ToOrderStatistics';

@Injectable({
  providedIn: 'root'
})
export class ToOrderStatisticsService extends AppRestService<ToOrderStatistics> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getOrderStatistics() {
    return this.get(new HttpParams(), "order/statistics");
  }

}
