import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { ServiceFamily } from '../../quotation/model/ServiceFamily';

@Injectable({
  providedIn: 'root'
})
export class ServiceFamilyService extends AppRestService<ServiceFamily>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getServiceFamilies() {
    return this.getList(new HttpParams(), "service-families");
  }
  
   addOrUpdateServiceFamily(serviceFamily: ServiceFamily) {
    return this.addOrUpdate(new HttpParams(), "service-family", serviceFamily, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
