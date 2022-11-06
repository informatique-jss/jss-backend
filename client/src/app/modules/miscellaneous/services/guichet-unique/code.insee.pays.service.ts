import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CodeInseePays } from 'src/app/modules/quotation/model/guichet-unique/referentials/CodeInseePays';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class CodeInseePaysService extends AppRestService<CodeInseePays>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getCodeInseePays() {
    return this.getList(new HttpParams(), 'code-insee-pays');
  }

}                        
