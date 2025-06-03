import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { DomiciliationContractType } from '../model/DomiciliationContractType';

@Injectable({
  providedIn: 'root'
})
export class DomiciliationContractTypeService extends AppRestService<DomiciliationContractType> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getContractTypes() {
    return this.getListCached(new HttpParams(), "domiciliation-contract-types");
  }
}
