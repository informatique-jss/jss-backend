import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { TransfertFundsType } from '../../quotation/model/TransfertFundsType';

@Injectable({
  providedIn: 'root'
})
export class TransfertFundsTypeService extends AppRestService<TransfertFundsType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getTransfertFundsTypes() {
    return this.getListCached(new HttpParams(), "transfert-fund-types");
  }

  addOrUpdateTransfertFundsType(transfertFundsType: TransfertFundsType) {
    this.clearListCache(new HttpParams(), "transfert-fund-types");
    return this.addOrUpdate(new HttpParams(), "transfert-fund-type", transfertFundsType, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
