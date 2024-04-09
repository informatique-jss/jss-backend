import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { DomiciliationFee } from '../model/DomiciliationFee';

@Injectable({
  providedIn: 'root'
})
export class DomiciliationFeeService extends AppRestService<DomiciliationFee> {
  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  addDomiciliationFee(newFee: DomiciliationFee) {
    return this.addOrUpdate(new HttpParams(), "domiciliation/fee/add", newFee);
  }

  deleteDomiciliationFee(fee: DomiciliationFee) {
    return this.get(new HttpParams().set("domiciliationFeeId", fee.id), "domiciliation/fee/delete");
  }

}
