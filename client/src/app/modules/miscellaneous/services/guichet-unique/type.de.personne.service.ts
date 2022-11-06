import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TypeDePersonne } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeDePersonne';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class TypeDePersonneService extends AppRestService<TypeDePersonne>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getTypeDePersonne() {
    return this.getList(new HttpParams(), 'type-de-personne');
  }

}                        
