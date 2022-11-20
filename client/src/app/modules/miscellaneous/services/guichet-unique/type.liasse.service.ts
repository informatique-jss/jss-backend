import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TypeLiasse } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeLiasse';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class TypeLiasseService extends AppRestService<TypeLiasse>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getTypeLiasse() {
    return this.getListCached(new HttpParams(), 'type-liasse');
  }

}
