import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { SimpleProvisionStatus } from '../model/SimpleProvisonStatus';

@Injectable({
  providedIn: 'root'
})
export class SimpleProvisionStatusService extends AppRestService<SimpleProvisionStatus> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getSimpleProvisionStatus() {
    // TODO : check if works
    return this.getList(new HttpParams(), "simple-provision-status");
  }

}
