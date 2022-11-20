import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NatureGerance } from 'src/app/modules/quotation/model/guichet-unique/referentials/NatureGerance';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class NatureGeranceService extends AppRestService<NatureGerance>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getNatureGerance() {
    return this.getListCached(new HttpParams(), 'nature-gerance');
  }

}
