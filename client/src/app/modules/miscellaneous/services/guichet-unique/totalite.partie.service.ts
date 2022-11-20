import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TotalitePartie } from 'src/app/modules/quotation/model/guichet-unique/referentials/TotalitePartie';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class TotalitePartieService extends AppRestService<TotalitePartie>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getTotalitePartie() {
    return this.getListCached(new HttpParams(), 'totalite-partie');
  }

}
