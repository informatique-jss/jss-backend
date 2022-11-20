import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SituationMatrimoniale } from 'src/app/modules/quotation/model/guichet-unique/referentials/SituationMatrimoniale';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class SituationMatrimonialeService extends AppRestService<SituationMatrimoniale>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getSituationMatrimoniale() {
    return this.getListCached(new HttpParams(), 'situation-matrimoniale');
  }

}
