import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MotifCessation } from 'src/app/modules/quotation/model/guichet-unique/referentials/MotifCessation';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class MotifCessationService extends AppRestService<MotifCessation>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getMotifCessation() {
    return this.getListCached(new HttpParams(), 'motif-cessation');
  }

}
