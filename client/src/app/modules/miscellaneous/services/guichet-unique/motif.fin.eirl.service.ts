import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MotifFinEirl } from 'src/app/modules/quotation/model/guichet-unique/referentials/MotifFinEirl';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class MotifFinEirlService extends AppRestService<MotifFinEirl>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getMotifFinEirl() {
    return this.getList(new HttpParams(), 'motif-fin-eirl');
  }

}                        
