import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OrigineFusionScission } from 'src/app/modules/quotation/model/guichet-unique/referentials/OrigineFusionScission';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class OrigineFusionScissionService extends AppRestService<OrigineFusionScission>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getOrigineFusionScission() {
    return this.getListCached(new HttpParams(), 'origine-fusion-scission');
  }

}
