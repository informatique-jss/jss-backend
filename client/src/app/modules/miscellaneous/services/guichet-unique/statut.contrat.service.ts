import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { StatutContrat } from 'src/app/modules/quotation/model/guichet-unique/referentials/StatutContrat';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class StatutContratService extends AppRestService<StatutContrat>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getStatutContrat() {
    return this.getListCached(new HttpParams(), 'statut-contrat');
  }

}
