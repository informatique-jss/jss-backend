import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { Responsable } from '../model/Responsable';

@Injectable({
  providedIn: 'root'
})
export class ResponsableService extends AppRestService<Responsable> {

  constructor(http: HttpClient) {
    super(http, "profile");
  }

  getPotentialUserScope() {
    return this.getListCached(new HttpParams(), "user/scope/possible");
  }
}
