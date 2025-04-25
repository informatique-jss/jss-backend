import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { ServiceFieldType } from '../model/ServiceFieldType';
import { ServiceType } from '../model/ServiceType';

@Injectable({
  providedIn: 'root'
})
export class ServiceFieldTypeService extends AppRestService<ServiceFieldType> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getServiceFieldTypesByServiceType(serviceType: ServiceType) {
    return this.getListCached(new HttpParams().set("serviceTypeId", serviceType.id), "service-field-types/service-type");
  }
}
