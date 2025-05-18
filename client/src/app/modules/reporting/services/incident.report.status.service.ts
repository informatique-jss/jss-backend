import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { IncidentReportStatus } from '../../reporting/model/IncidentReportStatus';

@Injectable({
  providedIn: 'root'
})
export class IncidentReportStatusService extends AppRestService<IncidentReportStatus>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getIncidentReportStatusList() {
    return this.getList(new HttpParams(), "incident-report-status-list");
  }
  
   addOrUpdateIncidentReportStatus(incidentReportStatus: IncidentReportStatus) {
    return this.addOrUpdate(new HttpParams(), "incident-report-status", incidentReportStatus, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
