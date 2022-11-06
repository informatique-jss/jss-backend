import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { QualiteNonSedentaire } from 'src/app/modules/quotation/model/guichet-unique/referentials/QualiteNonSedentaire';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class QualiteNonSedentaireService extends AppRestService<QualiteNonSedentaire>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getQualiteNonSedentaire() {
    return this.getList(new HttpParams(), 'qualite-non-sedentaire');
  }

}                        
