import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RoleContrat } from 'src/app/modules/quotation/model/guichet-unique/referentials/RoleContrat';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class RoleContratService extends AppRestService<RoleContrat>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getRoleContrat() {
    return this.getListCached(new HttpParams(), 'role-contrat');
  }

}
