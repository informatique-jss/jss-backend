import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Events } from 'src/app/modules/quotation/model/guichet-unique/referentials/Events';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class EventsService extends AppRestService<Events>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getEvents() {
    return this.getListCached(new HttpParams(), 'events');
  }

}
