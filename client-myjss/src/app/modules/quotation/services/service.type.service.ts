import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { ServiceType } from '../../my-account/model/ServiceType';

@Injectable({
  providedIn: 'root'
})
export class ServiceTypeService extends AppRestService<ServiceType> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getServiceTypesForFamily(idServiceFamily: number) {
    return this.getListCached(new HttpParams().set("idServiceFamily", idServiceFamily), "service-type/service-family");
  }
  getServiceTypeWithIsMandatoryDocuments(serviceType: ServiceType, isFetchOnlyMandatoryDocuments: boolean) {
    return this.get(new HttpParams().set("serviceTypeId", serviceType.id).set("isFetchOnlyMandatoryDocuments", isFetchOnlyMandatoryDocuments), "service-type");
  }
}
