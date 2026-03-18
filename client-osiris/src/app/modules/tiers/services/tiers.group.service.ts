import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { TiersGroup } from '../../profile/model/TiersGroup';

@Injectable({
  providedIn: 'root'
})
export class TiersGroupService extends AppRestService<TiersGroup> {

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getTiersGroups() {
    return this.getList(new HttpParams(), "tiers-groups");
  }

}
