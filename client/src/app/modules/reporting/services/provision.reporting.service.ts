import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { ProvisionReporting } from '../model/ProvisionReporting';

@Injectable({
  providedIn: 'root'
})
export class ProvisionReportingService extends AppRestService<ProvisionReporting>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getProvisionReporting() {
    return this.getList(new HttpParams(), "provision");
  }

}
