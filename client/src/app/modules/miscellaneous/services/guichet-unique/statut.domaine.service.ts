import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { StatutDomaine } from 'src/app/modules/quotation/model/guichet-unique/referentials/StatutDomaine';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class StatutDomaineService extends AppRestService<StatutDomaine>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getStatutDomaine() {
    return this.getList(new HttpParams(), 'statut-domaine');
  }

}                        
