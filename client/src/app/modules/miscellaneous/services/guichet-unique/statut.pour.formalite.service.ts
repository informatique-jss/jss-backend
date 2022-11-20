import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { StatutPourFormalite } from 'src/app/modules/quotation/model/guichet-unique/referentials/StatutPourFormalite';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class StatutPourFormaliteService extends AppRestService<StatutPourFormalite>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getStatutPourFormalite() {
    return this.getListCached(new HttpParams(), 'statut-pour-formalite');
  }

}
