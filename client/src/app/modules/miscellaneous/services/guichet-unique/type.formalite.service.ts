import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TypeFormalite } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeFormalite';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class TypeFormaliteService extends AppRestService<TypeFormalite>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getTypeFormalite() {
    return this.getListCached(new HttpParams(), 'type-formalite');
  }

}
