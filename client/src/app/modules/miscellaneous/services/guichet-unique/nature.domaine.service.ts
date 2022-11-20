import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NatureDomaine } from 'src/app/modules/quotation/model/guichet-unique/referentials/NatureDomaine';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class NatureDomaineService extends AppRestService<NatureDomaine>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getNatureDomaine() {
    return this.getListCached(new HttpParams(), 'nature-domaine');
  }

}
