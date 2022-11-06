import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CodeInseePaysNaissance } from 'src/app/modules/quotation/model/guichet-unique/referentials/CodeInseePaysNaissance';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class CodeInseePaysNaissanceService extends AppRestService<CodeInseePaysNaissance>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getCodeInseePaysNaissance() {
    return this.getList(new HttpParams(), 'code-insee-pays-naissance');
  }

}                        
