import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { Civility } from '../../profile/model/Civility';

@Injectable({
  providedIn: 'root'
})
export class CivilityService extends AppRestService<Civility> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getCivilities() {
    return this.getListCached(new HttpParams(), "civilities");
  }


}
