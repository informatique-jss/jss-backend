import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DestinationLocationGeranceMand } from 'src/app/modules/quotation/model/guichet-unique/referentials/DestinationLocationGeranceMand';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class DestinationLocationGeranceMandService extends AppRestService<DestinationLocationGeranceMand>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getDestinationLocationGeranceMand() {
    return this.getListCached(new HttpParams(), 'destination-location-gerance-mand');
  }

}
