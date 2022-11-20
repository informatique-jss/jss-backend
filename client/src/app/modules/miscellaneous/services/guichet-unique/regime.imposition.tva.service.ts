import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RegimeImpositionTVA } from 'src/app/modules/quotation/model/guichet-unique/referentials/RegimeImpositionTVA';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class RegimeImpositionTVAService extends AppRestService<RegimeImpositionTVA>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getRegimeImpositionTVA() {
    return this.getListCached(new HttpParams(), 'regime-imposition-tva');
  }

}
