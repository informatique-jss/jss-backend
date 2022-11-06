import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ModalitesDeControle } from 'src/app/modules/quotation/model/guichet-unique/referentials/ModalitesDeControle';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class ModalitesDeControleService extends AppRestService<ModalitesDeControle>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getModalitesDeControle() {
    return this.getList(new HttpParams(), 'modalites-de-controle');
  }

}                        
