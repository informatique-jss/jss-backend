import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { IncidentReport } from '../../reporting/model/IncidentReport';

@Injectable({
  providedIn: 'root'
})
export class IncidentReportService extends AppRestService<IncidentReport> {

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  addOrUpdateIncidentReport(incidentReport: IncidentReport) {
    return this.addOrUpdate(new HttpParams(), "incident-report", incidentReport, "Incident enregistré", "Erreur lors de l'enregistrement");
  }

  searchIncidentReport(employeeIds: number[], statusIds: number[]) {
    return this.getList(new HttpParams().set("employeeIds", employeeIds.join(",")).set("statusIds", statusIds.join(',')), 'incident-report/search');
  }

  getIncidentReportsForCustomerOrder(idCustomerOrder: number) {
    return this.getList(new HttpParams().set("idCustomerOrder", idCustomerOrder), 'incident-report/order');
  }
}
