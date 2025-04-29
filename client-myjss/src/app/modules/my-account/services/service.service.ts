import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { Affaire } from '../model/Affaire';
import { AssoAffaireOrder } from '../model/AssoAffaireOrder';
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

  addOrUpdateServices(services: Service[], affaireId: number, affaireOrderId: number) {
    return this.postList(new HttpParams().set("affaireId", affaireId).set("affaireOrderId", affaireOrderId), "services", services);
  }

  getServiceForServiceTypeAndAffaire(service: ServiceType, affaire: Affaire) {
    return this.get(new HttpParams().set("idAffaire", affaire.id).set("serviceTypeId", service.id), "service-types/provisions");
  }

  addServiceToAssoAffaireOrder(service: ServiceType, asso: AssoAffaireOrder) {
    return this.get(new HttpParams().set("serviceTypeId", service.id).set("assoAffaireOrderId", asso.id), "service/add");
  }
}
