import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Regie } from '../model/Regie';

@Injectable({
  providedIn: 'root'
})
export class RegieService extends AppRestService<Regie>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getRegies() {
    return this.getListCached(new HttpParams(), "regies");
  }

  addOrUpdateRegie(regie: Regie) {
    this.clearListCache(new HttpParams(), "regies");
    return this.addOrUpdate(new HttpParams(), "regie", regie, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
