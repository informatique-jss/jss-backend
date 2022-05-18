import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Civility } from '../../miscellaneous/model/Civility';

@Injectable({
  providedIn: 'root'
})
export class CivilityService extends AppRestService<Civility>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getCivilities() {
    return this.getList(new HttpParams(), "civilities");
  }

}
