import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Service } from '../model/Service';

@Injectable({
  providedIn: 'root'
})
export class ServiceService extends AppRestService<Service>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getServices() {
    return this.getList(new HttpParams(), "services");
  }

  addOrUpdateService(service: Service) {
    return this.addOrUpdate(new HttpParams(), "service", service, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
