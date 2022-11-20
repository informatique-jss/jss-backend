import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LieuDeLiquidation } from 'src/app/modules/quotation/model/guichet-unique/referentials/LieuDeLiquidation';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class LieuDeLiquidationService extends AppRestService<LieuDeLiquidation>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getLieuDeLiquidation() {
    return this.getListCached(new HttpParams(), 'lieu-de-liquidation');
  }

}
