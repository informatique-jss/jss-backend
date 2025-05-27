import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { DashboardUserStatistics } from '../model/DashboardUserStatistics';

@Injectable({
  providedIn: 'root'
})
export class DashboardUserStatisticsService extends AppRestService<DashboardUserStatistics> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getDashboardUserStatistics() {
    return this.get(new HttpParams(), "dashboard/user/statistics");
  }
}
