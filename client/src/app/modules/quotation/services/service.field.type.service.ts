import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { ServiceFieldType } from '../../quotation/model/ServiceFieldType';

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

  addOrUpdateServiceFieldType(serviceFieldType: ServiceFieldType) {
    return this.addOrUpdate(new HttpParams(), "service-field-type", serviceFieldType, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
