import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
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

  getResponsable(idResponsable: number) {
    return this.get(new HttpParams().set("idResponsable", idResponsable), "responsable");
  }

  updateAcceptTermsForCurrentUser() {
    return this.get(new HttpParams(), "responsable/accept-terms");
  }
}
