import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DiffusionINSEE } from 'src/app/modules/quotation/model/guichet-unique/referentials/DiffusionINSEE';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class DiffusionINSEEService extends AppRestService<DiffusionINSEE>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getDiffusionINSEE() {
    return this.getListCached(new HttpParams(), 'diffusion-insee');
  }

}
