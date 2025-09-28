import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { ReportingDashboard } from '../model/ReportingDashboard';

@Injectable({
  providedIn: 'root'
})
export class ReportingDashboardService extends AppRestService<ReportingDashboard> {

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getReportingDashboardsForCurrentUser() {
    return this.getList(new HttpParams(), "reporting-dashboards/current");
  }

  getReportingDashboardById(id: number) {
    return this.get(new HttpParams().set("id", id), "reporting-dashboards/id");
  }

}
