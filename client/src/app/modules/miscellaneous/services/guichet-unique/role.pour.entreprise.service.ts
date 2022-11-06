import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RolePourEntreprise } from 'src/app/modules/quotation/model/guichet-unique/referentials/RolePourEntreprise';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class RolePourEntrepriseService extends AppRestService<RolePourEntreprise>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getRolePourEntreprise() {
    return this.getList(new HttpParams(), 'role-pour-entreprise');
  }

}                        
