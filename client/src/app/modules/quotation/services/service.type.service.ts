import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { ServiceType } from '../model/ServiceType';

@Injectable({
  providedIn: 'root'
})
export class ServiceTypeService extends AppRestService<ServiceType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getServiceTypes() {
    return this.getList(new HttpParams(), "service-types");
  }

  addOrUpdateServiceType(service: ServiceType) {
    return this.addOrUpdate(new HttpParams(), "service-type", service, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
