import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RegimeImpositionBenefices2 } from 'src/app/modules/quotation/model/guichet-unique/referentials/RegimeImpositionBenefices2';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class RegimeImpositionBenefices2Service extends AppRestService<RegimeImpositionBenefices2>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getRegimeImpositionBenefices2() {
    return this.getList(new HttpParams(), 'regime-imposition-benefices2');
  }

}                        
