import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CodePays } from 'src/app/modules/quotation/model/guichet-unique/referentials/CodePays';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class CodePaysService extends AppRestService<CodePays>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getCodePays() {
    return this.getList(new HttpParams(), 'code-pays');
  }

}                        
