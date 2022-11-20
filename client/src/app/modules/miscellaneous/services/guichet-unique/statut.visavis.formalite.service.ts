import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { StatutVisAVisFormalite } from 'src/app/modules/quotation/model/guichet-unique/referentials/StatutVisAVisFormalite';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class StatutVisAVisFormaliteService extends AppRestService<StatutVisAVisFormalite>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getStatutVisAVisFormalite() {
    return this.getListCached(new HttpParams(), 'statut-visavis-formalite');
  }

}
