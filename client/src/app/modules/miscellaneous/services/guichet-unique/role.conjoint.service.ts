import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RoleConjoint } from 'src/app/modules/quotation/model/guichet-unique/referentials/RoleConjoint';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class RoleConjointService extends AppRestService<RoleConjoint>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getRoleConjoint() {
    return this.getListCached(new HttpParams(), 'role-conjoint');
  }

}
