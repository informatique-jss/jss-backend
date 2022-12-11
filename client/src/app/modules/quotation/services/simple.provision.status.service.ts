import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { SimpleProvisionStatus } from '../model/SimpleProvisonStatus';

@Injectable({
  providedIn: 'root'
})
export class SimpleProvisionStatusService extends AppRestService<SimpleProvisionStatus>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getSimpleProvisionStatus() {
    return this.getList(new HttpParams(), "simple-provision-status");
  }

  addOrUpdateSimpleProvisonStatus(simpleProvisonStatus: SimpleProvisionStatus) {
    return this.addOrUpdate(new HttpParams(), "simple-provision-status", simpleProvisonStatus, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
