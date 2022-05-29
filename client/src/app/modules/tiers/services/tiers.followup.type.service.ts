import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { TiersFollowupType } from '../../tiers/model/TiersFollowupType';

@Injectable({
  providedIn: 'root'
})
export class TiersFollowupTypeService extends AppRestService<TiersFollowupType>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getTiersFollowupTypes() {
    return this.getList(new HttpParams(), "tiers-followup-types");
  }

}
