import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { Service } from '../model/Service';
import { ServiceType } from '../model/ServiceType';


@Injectable({
  providedIn: 'root'
})
export class ServiceService extends AppRestService<Service> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  addOrUpdateService(service: Service) {
    return this.addOrUpdate(new HttpParams(), "service", service);
  }

  addOrUpdateServices(services: ServiceType[], affaireOrderId: number) {
    return this.getList(new HttpParams().set("affaireOrderId", affaireOrderId).set("serviceTypeIds", services.map(s => s.id).join(",")), "services");
  }

  getServiceForServiceType(services: ServiceType[]) {
    return this.getList(new HttpParams().set("serviceTypeIds", services.map(s => s.id).join(",")), "service-types/provisions");
  }

  deleteService(service: Service) {
    return this.get(new HttpParams().set("idService", service.id), "service/delete");
  }
}
