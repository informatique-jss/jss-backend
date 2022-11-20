import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TypeVoie } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeVoie';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class TypeVoieService extends AppRestService<TypeVoie>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getTypeVoie() {
    return this.getListCached(new HttpParams(), 'type-voie');
  }

}
