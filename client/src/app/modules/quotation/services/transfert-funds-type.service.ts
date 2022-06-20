import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { TransfertFundsType } from '../../quotation/model/TransfertFundsType';

@Injectable({
  providedIn: 'root'
})
export class TransfertFundsTypeService extends AppRestService<TransfertFundsType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getTransfertFundsTypes() {
    return this.getList(new HttpParams(), "transfert-fund-types");
  }

}
