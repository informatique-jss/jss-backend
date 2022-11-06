import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PeriodiciteEtOptionsParticulie } from 'src/app/modules/quotation/model/guichet-unique/referentials/PeriodiciteEtOptionsParticulie';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class PeriodiciteEtOptionsParticulieService extends AppRestService<PeriodiciteEtOptionsParticulie>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getPeriodiciteEtOptionsParticulie() {
    return this.getList(new HttpParams(), 'periodicite-et-options-particulie');
  }

}                        
