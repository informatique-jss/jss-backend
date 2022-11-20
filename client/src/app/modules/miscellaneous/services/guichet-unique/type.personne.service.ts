import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TypePersonne } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypePersonne';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class TypePersonneService extends AppRestService<TypePersonne>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getTypePersonne() {
    return this.getListCached(new HttpParams(), 'type-personne');
  }

}
