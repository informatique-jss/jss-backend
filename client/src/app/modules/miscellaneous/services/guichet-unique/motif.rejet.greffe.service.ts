import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MotifRejetGreffe } from 'src/app/modules/quotation/model/guichet-unique/referentials/MotifRejetGreffe';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class MotifRejetGreffeService extends AppRestService<MotifRejetGreffe>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getMotifRejetGreffe() {
    return this.getList(new HttpParams(), 'motif-rejet-greffe');
  }

}                        
