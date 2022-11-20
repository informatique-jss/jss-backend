import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Region } from '../../miscellaneous/model/Region';

@Injectable({
  providedIn: 'root'
})
export class RegionService extends AppRestService<Region>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getRegions() {
    return this.getListCached(new HttpParams(), "regions");
  }

  addOrUpdateRegion(region: Region) {
    this.clearListCache(new HttpParams(), "regions");
    return this.addOrUpdate(new HttpParams(), "region", region, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
