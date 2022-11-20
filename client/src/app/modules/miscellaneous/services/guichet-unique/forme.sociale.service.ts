import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FormeSociale } from 'src/app/modules/quotation/model/guichet-unique/referentials/FormeSociale';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class FormeSocialeService extends AppRestService<FormeSociale>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getFormeSociale() {
    return this.getListCached(new HttpParams(), 'forme-sociale');
  }

}
