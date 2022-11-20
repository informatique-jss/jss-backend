import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { StatutPraticien } from 'src/app/modules/quotation/model/guichet-unique/referentials/StatutPraticien';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class StatutPraticienService extends AppRestService<StatutPraticien>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getStatutPraticien() {
    return this.getListCached(new HttpParams(), 'statut-praticien');
  }

}
