import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { ServiceFamily } from '../model/ServiceFamily';

@Injectable({
  providedIn: 'root'
})
export class ServiceFamilyService extends AppRestService<ServiceFamily> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getServiceFamiliesForFamilyGroup(idServiceFamilyGroup: number) {
    return this.getList(new HttpParams().set("idServiceFamilyGroup", idServiceFamilyGroup), "service-families/service-group");
  }

  getServiceFamiliesForMandatoryDocuments() {
    return this.getList(new HttpParams(), "service-families/mandatory-documents");
  }
}
