import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MotifTrasnfert } from 'src/app/modules/quotation/model/guichet-unique/referentials/MotifTrasnfert';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class MotifTrasnfertService extends AppRestService<MotifTrasnfert>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getMotifTrasnfert() {
    return this.getListCached(new HttpParams(), 'motif-trasnfert');
  }

}
