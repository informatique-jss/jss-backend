import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SituationVisAVisMsa } from 'src/app/modules/quotation/model/guichet-unique/referentials/SituationVisAVisMsa';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class SituationVisAVisMsaService extends AppRestService<SituationVisAVisMsa>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getSituationVisAVisMsa() {
    return this.getList(new HttpParams(), 'situation-visavis-msa');
  }

}                        
