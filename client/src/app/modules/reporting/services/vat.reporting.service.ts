import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { VatReporting } from '../model/VatReporting';

@Injectable({
  providedIn: 'root'
})
export class VatReportingService extends AppRestService<VatReporting>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getVatReporting() {
    return this.getList(new HttpParams(), "vat");
  }

}
