import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Confrere } from '../../quotation/model/Confrere';

@Injectable({
  providedIn: 'root'
})
export class ConfrereService extends AppRestService<Confrere>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getConfreres() {
    return this.getListCached(new HttpParams(), "confreres");
  }

  addOrUpdateConfrere(confrere: Confrere) {
    this.clearListCache(new HttpParams(), "confreres");
    return this.addOrUpdate(new HttpParams(), "confrere", confrere, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
