import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { SalesReclamationCause } from '../../miscellaneous/model/SalesReclamationCause';

@Injectable({
  providedIn: 'root'
})
export class SalesReclamationCauseService extends AppRestService<SalesReclamationCause>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getReclamationCauses() {
    return this.getListCached(new HttpParams(), "sales-reclamation-causes");
  }

   addOrUpdateReclamationCause(salesReclamationCause: SalesReclamationCause) {
    return this.addOrUpdate(new HttpParams(), "sales-reclamation-cause", salesReclamationCause, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
