import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Formalite } from '../model/Formalite';

@Injectable({
  providedIn: 'root'
})
export class FormaliteService extends AppRestService<Formalite> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  addOrUpdateFormalite(formalite: Formalite) {
    return this.addOrUpdate(new HttpParams(), "formalite/update", formalite, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
