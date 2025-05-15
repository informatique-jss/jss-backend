import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { Candidacy } from '../../profile/model/Candidacy';

@Injectable({
  providedIn: 'root'
})
export class CandidacyService extends AppRestService<Candidacy> {

  constructor(http: HttpClient) {
    super(http, "crm");
  }

  addOrUpdateCandidacy(candidacy: Candidacy) {
    return this.addOrUpdate(new HttpParams(), "subscribe/candidacy", candidacy, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
