import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Civility } from '../../miscellaneous/model/Civility';

@Injectable({
  providedIn: 'root'
})
export class CivilityService extends AppRestService<Civility>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getCivilities() {
    return this.getListCached(new HttpParams(), "civilities");
  }

  addOrUpdateCivility(civility: Civility) {
    this.clearListCache(new HttpParams(), "civilities");
    return this.addOrUpdate(new HttpParams(), "civility", civility, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
