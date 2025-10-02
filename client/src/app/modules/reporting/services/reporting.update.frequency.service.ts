import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { ReportingUpdateFrequency } from '../model/ReportingUpdateFrequency';

@Injectable({
  providedIn: 'root'
})
export class ReportingUpdateFrequencyService extends AppRestService<ReportingUpdateFrequency> {

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getReportingUpdateFrequencies() {
    return this.getList(new HttpParams(), "reporting-update-frequency");
  }

}
