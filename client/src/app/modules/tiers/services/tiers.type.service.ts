import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { TiersType } from '../model/TiersType';

@Injectable({
  providedIn: 'root'
})
export class TiersTypeService extends AppRestService<TiersType>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getTiersTypes() {
    return this.getList(new HttpParams(), "types");
  }

}
