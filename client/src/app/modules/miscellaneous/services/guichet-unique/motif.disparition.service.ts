import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MotifDisparition } from 'src/app/modules/quotation/model/guichet-unique/referentials/MotifDisparition';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class MotifDisparitionService extends AppRestService<MotifDisparition>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getMotifDisparition() {
    return this.getList(new HttpParams(), 'motif-disparition');
  }

}                        
