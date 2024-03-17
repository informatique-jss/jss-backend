import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { SalesComplain } from '../../miscellaneous/model/SalesComplain';

@Injectable({
  providedIn: 'root'
})
export class SalesComplainService extends AppRestService<SalesComplain>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getComplainsByTiersId(tiersId: number) {
    return this.getById("sales-complains", tiersId);
  }

   addOrUpdateComplain(salesComplain: SalesComplain) {
    return this.addOrUpdate(new HttpParams(), "sales-complain", salesComplain, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
