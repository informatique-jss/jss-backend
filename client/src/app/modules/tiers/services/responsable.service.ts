import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Responsable } from '../model/Responsable';

@Injectable({
  providedIn: 'root'
})
export class ResponsableService extends AppRestService<Responsable> {

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getResponsable(id: number) {
    return this.getById("responsable", id);
  }

  applyParametersDocumentToQuotation(idDocumentType: number, idResponsable: number) {
    return this.get(new HttpParams().set("idResponsable", idResponsable).set("idDocumentType", idDocumentType), "quotation/document/apply", "Les éléments des commandes et devis ont bien été mis à jour", "Erreur lors de l'application des paramètres");
  }
}
