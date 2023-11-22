import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { TurnoverVatReporting } from '../model/TurnoverVatReporting';

@Injectable({
  providedIn: 'root'
})
export class TurnoverVatReportingService extends AppRestService<TurnoverVatReporting>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getTurnoverVatReporting() {
    return this.getList(new HttpParams(), "turnover-vat");
  }

}
