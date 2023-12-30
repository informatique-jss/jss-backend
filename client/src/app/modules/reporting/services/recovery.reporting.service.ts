import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { RecoveryReporting } from '../model/RecoveryReporting';

@Injectable({
  providedIn: 'root'
})
export class RecoveryReportingService extends AppRestService<RecoveryReporting>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getRecoveryReporting(columns: string[]) {
    return this.getList(new HttpParams().set("columns", columns.join(",")), "recovery");
  }

}
