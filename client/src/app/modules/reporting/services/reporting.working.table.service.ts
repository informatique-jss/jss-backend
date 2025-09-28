import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { ReportingWorkingTable } from '../../reporting/model/ReportingWorkingTable';

@Injectable({
  providedIn: 'root'
})
export class ReportingWorkingTableService extends AppRestService<ReportingWorkingTable>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getReportingWorkingTables() {
    return this.getList(new HttpParams(), "reporting-working-tables");
  }
  
   addOrUpdateReportingWorkingTable(reportingWorkingTable: ReportingWorkingTable) {
    return this.addOrUpdate(new HttpParams(), "reporting-working-table", reportingWorkingTable, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
