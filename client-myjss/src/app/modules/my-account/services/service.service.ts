import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { Affaire } from '../model/Affaire';
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

  addOrUpdateServices(services: ServiceType[], affaireId: number, affaireOrderId: number) {
    return this.getList(new HttpParams().set("affaireId", affaireId).set("affaireOrderId", affaireOrderId).set("serviceTypeIds", services.map(s => s.id).join(",")), "services");
  }

  getServiceForServiceTypeAndAffaire(services: ServiceType[], affaire: Affaire) {
    return this.getList(new HttpParams().set("idAffaire", affaire.id).set("serviceTypeIds", services.map(s => s.id).join(",")), "service-types/provisions");
  }

  deleteService(service: Service) {
    return this.get(new HttpParams().set("idService", service.id), "service/delete");
  }
}
