import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { ServiceFieldType } from '../../my-account/model/ServiceFieldType';

@Injectable({
  providedIn: 'root'
})
export class ServiceFieldTypeService extends AppRestService<ServiceFieldType> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getServiceFieldTypes() {
    return this.getList(new HttpParams(), "service-field-types");
  }
}
