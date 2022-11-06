import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MotifRejetMsa } from 'src/app/modules/quotation/model/guichet-unique/referentials/MotifRejetMsa';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class MotifRejetMsaService extends AppRestService<MotifRejetMsa>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getMotifRejetMsa() {
    return this.getList(new HttpParams(), 'motif-rejet-msa');
  }

}                        
