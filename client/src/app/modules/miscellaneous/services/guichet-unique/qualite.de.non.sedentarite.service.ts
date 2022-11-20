import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { QualiteDeNonSedentarite } from 'src/app/modules/quotation/model/guichet-unique/referentials/QualiteDeNonSedentarite';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class QualiteDeNonSedentariteService extends AppRestService<QualiteDeNonSedentarite>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getQualiteDeNonSedentarite() {
    return this.getListCached(new HttpParams(), 'qualite-de-non-sedentarite');
  }

}
