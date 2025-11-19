import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { Webinar } from '../model/Webinar';

@Injectable({
  providedIn: 'root'
})
export class WebinarService extends AppRestService<Webinar> {

  constructor(http: HttpClient) {
    super(http, "crm");
  }

  getLastWebinar() {
    return this.get(new HttpParams(), 'webinar/last');
  }

  getNextWebinar() {
    return this.get(new HttpParams(), 'webinar/next');
  }
}