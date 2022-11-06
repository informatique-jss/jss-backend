import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { StatutFormalite } from 'src/app/modules/quotation/model/guichet-unique/referentials/StatutFormalite';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class StatutFormaliteService extends AppRestService<StatutFormalite>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getStatutFormalite() {
    return this.getList(new HttpParams(), 'statut-formalite');
  }

}                        
