import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TypePersonneContractante } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypePersonneContractante';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class TypePersonneContractanteService extends AppRestService<TypePersonneContractante>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getTypePersonneContractante() {
    return this.getListCached(new HttpParams(), 'type-personne-contractante');
  }

}
