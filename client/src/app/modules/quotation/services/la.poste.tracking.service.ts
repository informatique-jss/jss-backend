import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { LaPosteTracking } from '../model/la-poste/LaPosteTracking';

@Injectable({
  providedIn: 'root'
})
export class LaPosteTrackingService extends AppRestService<LaPosteTracking> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getLaPosteTrackingsByProvision(provisionId: number) {
    return this.getList(new HttpParams().set("provisionId", provisionId), "la-poste-trackings/provision");
  }

  addLaPosteTrackingOnProvision(trackingId: number, provisionId: number) {
    return this.get(new HttpParams().set("trackingId", trackingId).set("provisionId", provisionId), "la-poste-trackings/provision/add");
  }

  removeTrackingOnProvision(trackingId: number) {
    return this.get(new HttpParams().set("trackingId", trackingId), "la-poste-trackings/provision/remove");
  }

  checkTrackingId(trackingId: number) {
    return this.get(new HttpParams().set("trackingId", trackingId), "la-poste-trackings/check") as any as Observable<boolean>;
  }

}
