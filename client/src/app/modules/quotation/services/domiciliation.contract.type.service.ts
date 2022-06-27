import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { DomiciliationContractType } from '../model/DomiciliationContractType';

@Injectable({
  providedIn: 'root'
})
export class DomiciliationContractTypeService extends AppRestService<DomiciliationContractType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getContractTypes() {
    return this.getList(new HttpParams(), "domiciliation-contract-types");
  }

  addOrUpdateDomiciliationContractType(domiciliationContractType: DomiciliationContractType) {
    return this.addOrUpdate(new HttpParams(), "domiciliation-contract-type", domiciliationContractType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
