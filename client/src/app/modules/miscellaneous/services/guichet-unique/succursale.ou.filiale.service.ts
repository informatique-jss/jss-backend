import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SuccursaleOuFiliale } from 'src/app/modules/quotation/model/guichet-unique/referentials/SuccursaleOuFiliale';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class SuccursaleOuFilialeService extends AppRestService<SuccursaleOuFiliale>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getSuccursaleOuFiliale() {
    return this.getListCached(new HttpParams(), 'succursale-ou-filiale');
  }

}
