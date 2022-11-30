import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { CustomerOrder } from '../model/CustomerOrder';
import { InvoiceLabelResult } from '../model/InvoiceLabelResult';

@Injectable({
  providedIn: 'root'
})
export class InvoiceLabelResultService extends AppRestService<InvoiceLabelResult>{

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getInvoiceLabelComputeResult(customerOrder: CustomerOrder) {
    return this.postItem(new HttpParams(), "invoice/label/compute", customerOrder);
  }
}
