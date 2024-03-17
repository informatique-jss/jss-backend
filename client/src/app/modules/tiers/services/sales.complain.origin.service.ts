import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { SalesComplainOrigin } from '../../miscellaneous/model/SalesComplainOrigin';

@Injectable({
  providedIn: 'root'
})
export class SalesComplainOriginService extends AppRestService<SalesComplainOrigin>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getComplainOrigins() {
    return this.getListCached(new HttpParams(), "sales-complain-origins");
  }

   addOrUpdateComplainOrigin(salesComplainOrigin: SalesComplainOrigin) {
    return this.addOrUpdate(new HttpParams(), "sales-complain-origin", salesComplainOrigin, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
