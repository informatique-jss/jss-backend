import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { SalesComplainCause } from '../../miscellaneous/model/SalesComplainCause';

@Injectable({
  providedIn: 'root'
})
export class SalesComplainCauseService extends AppRestService<SalesComplainCause>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getComplainCauses() {
    return this.getListCached(new HttpParams(), "sales-complain-causes");
  }

   addOrUpdateComplainCause(salesComplainCause: SalesComplainCause) {
    return this.addOrUpdate(new HttpParams(), "sales-complain-cause", salesComplainCause, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
