import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { JeuneAgriculteur } from 'src/app/modules/quotation/model/guichet-unique/referentials/JeuneAgriculteur';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class JeuneAgriculteurService extends AppRestService<JeuneAgriculteur>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getJeuneAgriculteur() {
    return this.getList(new HttpParams(), 'jeune-agriculteur');
  }

}                        
