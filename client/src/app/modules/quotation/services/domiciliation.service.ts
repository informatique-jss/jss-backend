import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Domiciliation } from '../model/Domiciliation';
import { Provision } from '../model/Provision';

@Injectable({
  providedIn: 'root'
})
export class DomiciliationService extends AppRestService<Domiciliation> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  generateDomiciliationContract(provision: Provision) {
    return this.get(new HttpParams().set("provisionId", provision.id), "domiciliation/contract", "Contrat généré");
  }
}
