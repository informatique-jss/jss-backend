import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { ServiceFamilyGroup } from '../../quotation/model/ServiceFamilyGroup';

@Injectable({
  providedIn: 'root'
})
export class ServiceFamilyGroupService extends AppRestService<ServiceFamilyGroup>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getServiceFamilyGroups() {
    return this.getList(new HttpParams(), "service-family-groups");
  }
  
   addOrUpdateServiceFamilyGroup(serviceFamilyGroup: ServiceFamilyGroup) {
    return this.addOrUpdate(new HttpParams(), "service-family-group", serviceFamilyGroup, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
