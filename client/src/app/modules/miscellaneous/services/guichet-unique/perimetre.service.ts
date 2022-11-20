import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Perimetre } from 'src/app/modules/quotation/model/guichet-unique/referentials/Perimetre';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class PerimetreService extends AppRestService<Perimetre>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getPerimetre() {
    return this.getListCached(new HttpParams(), 'perimetre');
  }

}
