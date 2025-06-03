import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { BuildingDomiciliation } from '../model/BuildingDomiciliation';

@Injectable({
  providedIn: 'root'
})
export class BuildingDomiciliationService extends AppRestService<BuildingDomiciliation> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getBuildingDomiciliations() {
    return this.getListCached(new HttpParams(), "building-domiciliations");
  }
}
