import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { ReportingWidget } from '../../reporting/model/ReportingWidget';

@Injectable({
  providedIn: 'root'
})
export class ReportingWidgetService extends AppRestService<ReportingWidget>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getReportingWidgets() {
    return this.getList(new HttpParams(), "reporting-widgets");
  }
  
   addOrUpdateReportingWidget(reportingWidget: ReportingWidget) {
    return this.addOrUpdate(new HttpParams(), "reporting-widget", reportingWidget, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
