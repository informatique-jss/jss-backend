import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { QuotationLabelType } from '../../quotation/model/QuotationLabelType';

@Injectable({
  providedIn: 'root'
})
export class QuotationLabelTypeService extends AppRestService<QuotationLabelType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getQuotationLabelTypes() {
    return this.getList(new HttpParams(), "quotation-label-ypes");
  }

}
