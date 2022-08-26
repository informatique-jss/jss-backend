import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { QuotationLabelType } from '../../quotation/model/QuotationLabelType';

@Injectable({
  providedIn: 'root'
})
export class QuotationLabelTypeService extends AppRestService<QuotationLabelType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getQuotationLabelTypes() {
    return this.getList(new HttpParams(), "quotation-label-types");
  }

  addOrUpdateQuotationLabelType(quotationLabelType: QuotationLabelType) {
    return this.addOrUpdate(new HttpParams(), "quotation-label-type", quotationLabelType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
