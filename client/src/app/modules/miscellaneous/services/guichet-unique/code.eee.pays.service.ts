import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CodeEEEPays } from 'src/app/modules/quotation/model/guichet-unique/referentials/CodeEEEPays';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class CodeEEEPaysService extends AppRestService<CodeEEEPays>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getCodeEEEPays() {
    return this.getList(new HttpParams(), 'code-eee-pays');
  }

}                        
