import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { BuildingDomiciliation } from '../../quotation/model/BuildingDomiciliation';

@Injectable({
  providedIn: 'root'
})
export class BuildingDomiciliationService extends AppRestService<BuildingDomiciliation>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getBuildingDomiciliations() {
    return this.getListCached(new HttpParams(), "building-domiciliations");
  }

  addOrUpdateBuildingDomiciliation(buildingDomiciliation: BuildingDomiciliation) {
    this.clearListCache(new HttpParams(), "building-domiciliations");
    return this.addOrUpdate(new HttpParams(), "building-domiciliation", buildingDomiciliation, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
