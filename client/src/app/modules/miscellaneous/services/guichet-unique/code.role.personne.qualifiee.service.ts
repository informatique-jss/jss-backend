import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CodeRolePersonneQualifiee } from 'src/app/modules/quotation/model/guichet-unique/referentials/CodeRolePersonneQualifiee';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class CodeRolePersonneQualifieeService extends AppRestService<CodeRolePersonneQualifiee>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getCodeRolePersonneQualifiee() {
    return this.getList(new HttpParams(), 'code-role-personne-qualifiee');
  }

}                        
