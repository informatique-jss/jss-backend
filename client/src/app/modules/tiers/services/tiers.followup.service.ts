import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { TiersFollowup } from '../model/TiersFollowup';

@Injectable({
  providedIn: 'root'
})
export class TiersFollowupService extends AppRestService<TiersFollowup>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  addFollowup(tiersFollowup: TiersFollowup) {
    return this.postList(new HttpParams(), "tiers-followup", tiersFollowup);
  }

}
