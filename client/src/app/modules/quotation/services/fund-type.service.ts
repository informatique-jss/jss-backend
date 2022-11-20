import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { FundType } from '../../quotation/model/FundType';

@Injectable({
  providedIn: 'root'
})
export class FundTypeService extends AppRestService<FundType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getFundTypes() {
    return this.getListCached(new HttpParams(), "fund-types");
  }

  addOrUpdateFundType(fundType: FundType) {
    this.clearListCache(new HttpParams(), "fund-types");
    return this.addOrUpdate(new HttpParams(), "fund-type", fundType, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
