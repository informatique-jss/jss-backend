import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TypeDeStatuts } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeDeStatuts';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class TypeDeStatutsService extends AppRestService<TypeDeStatuts>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getTypeDeStatuts() {
    return this.getListCached(new HttpParams(), 'type-de-statuts');
  }

}
