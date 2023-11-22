import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { TurnoverReporting } from '../model/TurnoverReporting';

@Injectable({
  providedIn: 'root'
})
export class TurnoverReportingService extends AppRestService<TurnoverReporting>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getTurnoverReporting() {
    return this.getList(new HttpParams(), "turnover");
  }

}
