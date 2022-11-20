import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MineurNationalite } from 'src/app/modules/quotation/model/guichet-unique/referentials/MineurNationalite';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class MineurNationaliteService extends AppRestService<MineurNationalite>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getMineurNationalite() {
    return this.getListCached(new HttpParams(), 'mineur-nationalite');
  }

}
