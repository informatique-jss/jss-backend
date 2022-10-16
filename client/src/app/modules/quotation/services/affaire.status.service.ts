import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AffaireStatus } from '../../quotation/model/AffaireStatus';

@Injectable({
  providedIn: 'root'
})
export class AffaireStatusService extends AppRestService<AffaireStatus>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAffaireStatus() {
    return this.getList(new HttpParams(), "affaire-status-list");
  }
  
   addOrUpdateAffaireStatus(affaireStatus: AffaireStatus) {
    return this.addOrUpdate(new HttpParams(), "affaire-status", affaireStatus, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
