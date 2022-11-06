import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Role } from 'src/app/modules/quotation/model/guichet-unique/referentials/Role';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class RoleService extends AppRestService<Role>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getRole() {
    return this.getList(new HttpParams(), 'role');
  }

}                        
