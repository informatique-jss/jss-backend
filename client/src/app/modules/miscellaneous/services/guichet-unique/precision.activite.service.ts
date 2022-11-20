import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PrecisionActivite } from 'src/app/modules/quotation/model/guichet-unique/referentials/PrecisionActivite';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class PrecisionActiviteService extends AppRestService<PrecisionActivite>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getPrecisionActivite() {
    return this.getListCached(new HttpParams(), 'precision-activite');
  }

}
