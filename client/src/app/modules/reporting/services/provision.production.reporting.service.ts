import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { ProvisionProduction } from '../model/ProvisionProduction';

@Injectable({
  providedIn: 'root'
})
export class ProvisionProductionReportingService extends AppRestService<ProvisionProduction>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getProvisionProductionReporting() {
    return this.getList(new HttpParams(), "provision-production");
  }

}
