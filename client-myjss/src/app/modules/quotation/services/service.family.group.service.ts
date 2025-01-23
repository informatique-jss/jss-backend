import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { ServiceFamilyGroup } from '../model/ServiceFamilyGroup';

@Injectable({
  providedIn: 'root'
})
export class ServiceFamilyGroupService extends AppRestService<ServiceFamilyGroup> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getServiceFamilyGroups() {
    return this.getList(new HttpParams(), "service-family-groups");
  }
}
