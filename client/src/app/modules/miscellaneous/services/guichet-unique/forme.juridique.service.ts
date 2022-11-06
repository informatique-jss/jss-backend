import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FormeJuridique } from 'src/app/modules/quotation/model/guichet-unique/referentials/FormeJuridique';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class FormeJuridiqueService extends AppRestService<FormeJuridique>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getFormeJuridique() {
    return this.getList(new HttpParams(), 'forme-juridique');
  }

}                        
