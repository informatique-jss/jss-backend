import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NatureVoletSocial } from 'src/app/modules/quotation/model/guichet-unique/referentials/NatureVoletSocial';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class NatureVoletSocialService extends AppRestService<NatureVoletSocial>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getNatureVoletSocial() {
    return this.getList(new HttpParams(), 'nature-volet-social');
  }

}                        
