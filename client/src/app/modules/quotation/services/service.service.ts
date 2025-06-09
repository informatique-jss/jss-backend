import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Service } from '../../quotation/model/Service';
import { Affaire } from '../model/Affaire';
import { ServiceType } from '../model/ServiceType';

@Injectable({
  providedIn: 'root'
})
export class ServiceService extends AppRestService<Service> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getServices() {
    return this.getList(new HttpParams(), "services");
  }

  addOrUpdateService(service: Service) {
    return this.addOrUpdate(new HttpParams(), "service", service, "Enregistré", "Erreur lors de l'enregistrement");
  }

  deleteService(service: Service) {
    return this.get(new HttpParams().set("serviceId", service.id), "service/delete", "Service supprimé", "Erreur lors de la suppression");
  }

  getServiceForServiceTypeAndAffaire(serviceType: ServiceType, affaire: Affaire) {
    return this.get(new HttpParams().set("serviceTypeId", serviceType.id).set("idAffaire", affaire.id), "service-type/provision");
  }

  getServiceForMultiServiceTypesAndAffaire(serviceTypes: ServiceType[], customLabel: string | undefined, affaire: Affaire) {
    const ids = serviceTypes.map(st => st.id).join(',');
    return this.getList(new HttpParams().set("customLabel", customLabel + "").set("serviceTypeIds", ids).set("idAffaire", affaire.id + ""), "service-types/provisions");
  }

  modifyServiceType(service: Service, serviceTypes: ServiceType[]) {
    return this.get(new HttpParams().set("serviceTypeIds", serviceTypes.map(st => st.id).join(',')).set("serviceId", service.id), "service/modify");
  }

}
