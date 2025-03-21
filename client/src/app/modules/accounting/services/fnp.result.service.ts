import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { toIsoString } from 'src/app/libs/FormatHelper';
import { AppRestService } from 'src/app/services/appRest.service';
import { FnpResult } from '../model/FnpResult';

@Injectable({
  providedIn: 'root'
})
export class FnpResultService extends AppRestService<FnpResult> {

  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  getFnp(accountingDate: Date) {
    return this.getList(new HttpParams().set("accountingDate", toIsoString(accountingDate)), "fnp");
  }

}
