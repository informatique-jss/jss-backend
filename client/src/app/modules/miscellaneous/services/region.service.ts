import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Region } from '../../miscellaneous/model/Region';

@Injectable({
  providedIn: 'root'
})
export class RegionService extends AppRestService<Region>{

  constructor(http: HttpClient) {
    super(http, "Tiers");
  }

  getRegions() {
    return this.getList(new HttpParams(), "regions");
  }

}
