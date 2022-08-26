import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Affaire } from '../../quotation/model/Affaire';

@Injectable({
  providedIn: 'root'
})
export class AffaireService extends AppRestService<Affaire>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAffaire(affaireId: number) {
    return this.getById("affaire", affaireId);
  }

  addOrUpdateAffaire(affaire: Affaire) {
    return this.addOrUpdate(new HttpParams(), "affaire", affaire, "Affaire créée", "Erreur lors de l'enregistrement de l'affaire");
  }
}
