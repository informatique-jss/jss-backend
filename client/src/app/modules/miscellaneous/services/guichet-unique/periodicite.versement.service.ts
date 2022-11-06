import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PeriodiciteVersement } from 'src/app/modules/quotation/model/guichet-unique/referentials/PeriodiciteVersement';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class PeriodiciteVersementService extends AppRestService<PeriodiciteVersement>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getPeriodiciteVersement() {
    return this.getList(new HttpParams(), 'periodicite-versement');
  }

}                        
