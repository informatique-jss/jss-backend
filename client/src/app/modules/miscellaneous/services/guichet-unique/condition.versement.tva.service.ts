import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ConditionVersementTVA } from 'src/app/modules/quotation/model/guichet-unique/referentials/ConditionVersementTVA';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class ConditionVersementTVAService extends AppRestService<ConditionVersementTVA>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getConditionVersementTVA() {
    return this.getList(new HttpParams(), 'condition-versement-tva');
  }

}                        
