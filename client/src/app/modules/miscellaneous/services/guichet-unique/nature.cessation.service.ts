import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NatureCessation } from 'src/app/modules/quotation/model/guichet-unique/referentials/NatureCessation';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class NatureCessationService extends AppRestService<NatureCessation>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getNatureCessation() {
    return this.getList(new HttpParams(), 'nature-cessation');
  }

}                        
