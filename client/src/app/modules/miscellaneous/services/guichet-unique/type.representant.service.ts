import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TypeRepresentant } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeRepresentant';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class TypeRepresentantService extends AppRestService<TypeRepresentant>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getTypeRepresentant() {
    return this.getListCached(new HttpParams(), 'type-representant');
  }

}
