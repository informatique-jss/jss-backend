import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Destination } from 'src/app/modules/quotation/model/guichet-unique/referentials/Destination';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class DestinationService extends AppRestService<Destination>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getDestination() {
    return this.getListCached(new HttpParams(), 'destination');
  }

}
