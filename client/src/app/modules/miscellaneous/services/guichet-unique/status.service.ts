import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Status } from 'src/app/modules/quotation/model/guichet-unique/referentials/Status';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class StatusService extends AppRestService<Status>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getStatus() {
    return this.getList(new HttpParams(), 'status');
  }

}                        
