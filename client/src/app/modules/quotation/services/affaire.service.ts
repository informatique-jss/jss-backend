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

  getAffaires() {
    return this.getList(new HttpParams(), "affaires");
  }

  addOrUpdateAffaire(affaire: Affaire) {
    return this.addOrUpdate(new HttpParams(), "affaire", affaire, affaire.id ? "Affaire mise à jour" : "Affaire créée", "Erreur lors de l'enregistrement de l'affaire");
  }

  generateAffaireBySiret(siret: string) {
    return this.getList(new HttpParams().set("siret", siret), "siret");
  }

  generateAffaireBySiren(siren: string) {
    return this.getList(new HttpParams().set("siren", siren), "siren");
  }

  generateAffaireByRna(rna: string) {
    return this.getList(new HttpParams().set("rna", rna), "rna");
  }
}
