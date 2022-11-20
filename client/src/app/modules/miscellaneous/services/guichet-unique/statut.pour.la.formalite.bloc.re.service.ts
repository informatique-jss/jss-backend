import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { StatutPourLaFormaliteBlocRe } from 'src/app/modules/quotation/model/guichet-unique/referentials/StatutPourLaFormaliteBlocRe';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class StatutPourLaFormaliteBlocReService extends AppRestService<StatutPourLaFormaliteBlocRe>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getStatutPourLaFormaliteBlocRe() {
    return this.getListCached(new HttpParams(), 'statut-pour-la-formalite-bloc-re');
  }

}
