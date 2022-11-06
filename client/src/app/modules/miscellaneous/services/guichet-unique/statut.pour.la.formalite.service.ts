import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { StatutPourLaFormalite } from 'src/app/modules/quotation/model/guichet-unique/referentials/StatutPourLaFormalite';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class StatutPourLaFormaliteService extends AppRestService<StatutPourLaFormalite>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getStatutPourLaFormalite() {
    return this.getList(new HttpParams(), 'statut-pour-la-formalite');
  }

}                        
