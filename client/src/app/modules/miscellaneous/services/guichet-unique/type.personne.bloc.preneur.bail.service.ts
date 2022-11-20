import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TypePersonneBlocPreneurBail } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypePersonneBlocPreneurBail';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class TypePersonneBlocPreneurBailService extends AppRestService<TypePersonneBlocPreneurBail>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getTypePersonneBlocPreneurBail() {
    return this.getListCached(new HttpParams(), 'type-personne-bloc-preneur-bail');
  }

}
