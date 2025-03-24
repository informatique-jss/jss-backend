import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { toIsoString } from 'src/app/libs/FormatHelper';
import { AppRestService } from 'src/app/services/appRest.service';
import { FaeResult } from '../model/FaeResult';

@Injectable({
  providedIn: 'root'
})
export class FaeResultService extends AppRestService<FaeResult> {

  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  getFae(accountingDate: Date) {
    return this.getList(new HttpParams().set("accountingDate", toIsoString(accountingDate)), "fae");
  }

}
