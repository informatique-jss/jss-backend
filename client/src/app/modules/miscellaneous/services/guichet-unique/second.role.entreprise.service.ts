import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SecondRoleEntreprise } from 'src/app/modules/quotation/model/guichet-unique/referentials/SecondRoleEntreprise';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class SecondRoleEntrepriseService extends AppRestService<SecondRoleEntreprise>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getSecondRoleEntreprise() {
    return this.getList(new HttpParams(), 'second-role-entreprise');
  }

}                        
