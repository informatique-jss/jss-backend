import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { TiersReporting } from '../model/TiersReporting';

@Injectable({
  providedIn: 'root'
})
export class TiersReportingService extends AppRestService<TiersReporting>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getTiersReporting() {
    return this.getList(new HttpParams(), "tiers");
  }

}
