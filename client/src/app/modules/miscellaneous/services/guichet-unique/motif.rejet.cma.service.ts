import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MotifRejetCma } from 'src/app/modules/quotation/model/guichet-unique/referentials/MotifRejetCma';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class MotifRejetCmaService extends AppRestService<MotifRejetCma>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getMotifRejetCma() {
    return this.getListCached(new HttpParams(), 'motif-rejet-cma');
  }

}
