import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MineurLienParente } from 'src/app/modules/quotation/model/guichet-unique/referentials/MineurLienParente';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class MineurLienParenteService extends AppRestService<MineurLienParente>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getMineurLienParente() {
    return this.getList(new HttpParams(), 'mineur-lien-parente');
  }

}                        
