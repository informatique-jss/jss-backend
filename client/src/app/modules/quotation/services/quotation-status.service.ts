import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { QuotationStatus } from '../../quotation/model/QuotationStatus';

@Injectable({
  providedIn: 'root'
})
export class QuotationStatusService extends AppRestService<QuotationStatus>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getQuotationStatus() {
    return this.getListCached(new HttpParams(), "quotation-status");
  }

  getQuotationStatusByCode(status: QuotationStatus[], code: string) {
    if (status)
      for (let statu of status)
        if (statu.code == code)
          return statu;
    return null;
  }

}
