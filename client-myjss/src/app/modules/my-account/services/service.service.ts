import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { Service } from '../model/Service';


@Injectable({
  providedIn: 'root'
})
export class ServiceService extends AppRestService<Service> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  addOrUpdateServiceFields(service: Service) {
    return this.addOrUpdate(new HttpParams(), "service/fields", service);
  }
}
