import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CodeNationalite } from 'src/app/modules/quotation/model/guichet-unique/referentials/CodeNationalite';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class CodeNationaliteService extends AppRestService<CodeNationalite>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getCodeNationalite() {
    return this.getList(new HttpParams(), 'code-nationalite');
  }

}                        
