import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RoleEntreprise } from 'src/app/modules/quotation/model/guichet-unique/referentials/RoleEntreprise';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class RoleEntrepriseService extends AppRestService<RoleEntreprise>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getRoleEntreprise() {
    return this.getListCached(new HttpParams(), 'role-entreprise');
  }

}
