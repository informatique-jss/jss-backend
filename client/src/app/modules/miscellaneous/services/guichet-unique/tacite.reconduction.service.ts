import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TaciteReconduction } from 'src/app/modules/quotation/model/guichet-unique/referentials/TaciteReconduction';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class TaciteReconductionService extends AppRestService<TaciteReconduction>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getTaciteReconduction() {
    return this.getList(new HttpParams(), 'tacite-reconduction');
  }

}                        
