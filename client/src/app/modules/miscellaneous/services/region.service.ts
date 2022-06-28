import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Region } from '../../miscellaneous/model/Region';

@Injectable({
  providedIn: 'root'
})
export class RegionService extends AppRestService<Region>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getRegions() {
    return this.getList(new HttpParams(), "regions");
  }
  
   addOrUpdateRegion(region: Region) {
    return this.addOrUpdate(new HttpParams(), "region", region, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
