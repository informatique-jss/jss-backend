import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CodifNorme } from 'src/app/modules/quotation/model/guichet-unique/referentials/CodifNorme';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class CodifNormeService extends AppRestService<CodifNorme>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getCodifNorme() {
    return this.getListCached(new HttpParams(), 'codif-norme');
  }

}
