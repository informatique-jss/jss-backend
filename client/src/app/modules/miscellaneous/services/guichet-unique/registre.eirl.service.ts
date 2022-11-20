import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RegistreEirl } from 'src/app/modules/quotation/model/guichet-unique/referentials/RegistreEirl';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class RegistreEirlService extends AppRestService<RegistreEirl>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getRegistreEirl() {
    return this.getListCached(new HttpParams(), 'registre-eirl');
  }

}
