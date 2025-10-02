import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { ReportingDashboard } from '../../reporting/model/ReportingDashboard';

@Injectable({
  providedIn: 'root'
})
export class ReportingDashboardService extends AppRestService<ReportingDashboard>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getReportingDashboards() {
    return this.getList(new HttpParams(), "reporting-dashboards");
  }
  
   addOrUpdateReportingDashboard(reportingDashboard: ReportingDashboard) {
    return this.addOrUpdate(new HttpParams(), "reporting-dashboard", reportingDashboard, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
