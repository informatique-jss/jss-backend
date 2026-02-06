import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { PaySlipLineType } from '../../accounting/model/PaySlipLineType';

@Injectable({
  providedIn: 'root'
})
export class PaySlipLineTypeService extends AppRestService<PaySlipLineType>{

  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  getPaySlipLineTypes() {
    return this.getList(new HttpParams(), "pay-slip-line-types");
  }
  
   addOrUpdatePaySlipLineType(paySlipLineType: PaySlipLineType) {
    return this.addOrUpdate(new HttpParams(), "pay-slip-line-type", paySlipLineType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
