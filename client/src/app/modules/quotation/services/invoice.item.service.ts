import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { InvoiceItem } from '../model/InvoiceItem';
import { IQuotation } from '../model/IQuotation';

@Injectable({
  providedIn: 'root'
})
export class InvoiceItemService extends AppRestService<InvoiceItem>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getInvoiceItemsForQuotation(quotation: IQuotation) {
    return this.postList(new HttpParams(), "invoice-item/generate", quotation);
  }

}
