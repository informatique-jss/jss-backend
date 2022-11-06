import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MineurSexe } from 'src/app/modules/quotation/model/guichet-unique/referentials/MineurSexe';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class MineurSexeService extends AppRestService<MineurSexe>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getMineurSexe() {
    return this.getList(new HttpParams(), 'mineur-sexe');
  }

}                        
