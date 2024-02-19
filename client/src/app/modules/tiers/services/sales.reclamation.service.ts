import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { SalesReclamation } from '../../miscellaneous/model/SalesReclamation';

@Injectable({
  providedIn: 'root'
})
export class SalesReclamationService extends AppRestService<SalesReclamation>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getReclamationsByTiersId(tiersId: number) {
    console.log(tiersId)
    return this.getById("sales-reclamations", tiersId);
  }

   addOrUpdateReclamation(salesReclamation: SalesReclamation) {
    return this.addOrUpdate(new HttpParams(), "sales-reclamation", salesReclamation, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
