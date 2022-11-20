import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActiviteReguliere } from 'src/app/modules/quotation/model/guichet-unique/referentials/ActiviteReguliere';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class ActiviteReguliereService extends AppRestService<ActiviteReguliere>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getActiviteReguliere() {
    return this.getListCached(new HttpParams(), 'activite-reguliere');
  }

}
