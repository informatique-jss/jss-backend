import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RegimeImpositionBenefices } from 'src/app/modules/quotation/model/guichet-unique/referentials/RegimeImpositionBenefices';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class RegimeImpositionBeneficesService extends AppRestService<RegimeImpositionBenefices>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getRegimeImpositionBenefices() {
    return this.getListCached(new HttpParams(), 'regime-imposition-benefices');
  }

}
