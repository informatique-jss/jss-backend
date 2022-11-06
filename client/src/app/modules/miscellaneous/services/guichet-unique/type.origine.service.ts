import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TypeOrigine } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeOrigine';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class TypeOrigineService extends AppRestService<TypeOrigine>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getTypeOrigine() {
    return this.getList(new HttpParams(), 'type-origine');
  }

}                        
