import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DeviseCapital } from 'src/app/modules/quotation/model/guichet-unique/referentials/DeviseCapital';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class DeviseCapitalService extends AppRestService<DeviseCapital>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getDeviseCapital() {
    return this.getList(new HttpParams(), 'devise-capital');
  }

}                        
