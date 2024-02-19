import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { SalesReclamationOrigin } from '../../miscellaneous/model/SalesReclamationOrigin';

@Injectable({
  providedIn: 'root'
})
export class SalesReclamationOriginService extends AppRestService<SalesReclamationOrigin>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getReclamationOrigins() {
    return this.getListCached(new HttpParams(), "sales-reclamation-origins");
  }

   addOrUpdateReclamationOrigin(salesReclamationOrigin: SalesReclamationOrigin) {
    return this.addOrUpdate(new HttpParams(), "sales-reclamation-origin", salesReclamationOrigin, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
