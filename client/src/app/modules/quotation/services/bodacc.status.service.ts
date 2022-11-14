import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { BodaccStatus } from '../../quotation/model/BodaccStatus';

@Injectable({
  providedIn: 'root'
})
export class BodaccStatusService extends AppRestService<BodaccStatus>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getBodaccStatus() {
    return this.getList(new HttpParams(), "bodacc-status");
  }

}
