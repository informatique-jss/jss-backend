import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RegistreEirlDeLancienneEirl } from 'src/app/modules/quotation/model/guichet-unique/referentials/RegistreEirlDeLancienneEirl';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class RegistreEirlDeLancienneEirlService extends AppRestService<RegistreEirlDeLancienneEirl>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getRegistreEirlDeLancienneEirl() {
    return this.getListCached(new HttpParams(), 'registre-eirl-de-lancienne-eirl');
  }

}
