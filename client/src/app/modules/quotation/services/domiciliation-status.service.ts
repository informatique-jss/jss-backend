import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { DomiciliationStatus } from '../../quotation/model/DomiciliationStatus';

@Injectable({
  providedIn: 'root'
})
export class DomiciliationStatusService extends AppRestService<DomiciliationStatus>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getDomiciliationStatus() {
    return this.getListCached(new HttpParams(), "domiciliation-status");
  }
}
