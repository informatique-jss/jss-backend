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

  getServiceForMultiServiceTypesAndAffaire(serviceTypes: ServiceType[], affaire: Affaire) {
    return this.postItem(new HttpParams().set("idAffaire", affaire.id), "service-types/provisions", serviceTypes);
  }

  modifyServiceType(service: Service, serviceType: ServiceType) {

    return this.get(new HttpParams().set("serviceTypeId", serviceType.id).set("serviceId", service.id), "service/modify");
  }

  getServiceLabel(service: Service, displayGroupAndFamily: boolean, serviceTypeOther: ServiceType) {
    let label = "";
    if (service && service.serviceType) {
      if (displayGroupAndFamily)
        label = service.serviceType.serviceFamily.serviceFamilyGroup.label + " - ";
      label += service.serviceType.label
      if (service.serviceType.id == serviceTypeOther.id && service.customLabel && service.customLabel.trim().length > 0)
        return service.customLabel;
    }
    return label;
  }

}
