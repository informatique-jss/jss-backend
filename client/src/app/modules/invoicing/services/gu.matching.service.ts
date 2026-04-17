import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { toIsoString } from 'src/app/libs/FormatHelper';
import { AppRestService } from 'src/app/services/appRest.service';
import { GuMatchingResult } from '../model/GuMatchingResult';

@Injectable({
  providedIn: 'root'
})
export class GuMatchingService extends AppRestService<GuMatchingResult> {

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getInpiExtractAndOsirisMatchingResult(startDate: Date, endDate: Date) {
    return this.getList(new HttpParams().set("startDate", toIsoString(startDate)).set("endDate", toIsoString(endDate)), "gu-matching");
  }


}
