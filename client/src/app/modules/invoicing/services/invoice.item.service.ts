import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { InvoiceItem } from '../../quotation/model/InvoiceItem';

@Injectable({
  providedIn: 'root'
})
export class InvoiceItemService extends AppRestService<InvoiceItem> {

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }
  updateInvoiceItemFromInvoice(idInvoiceItem: number, newPreTaxPrice: number) {
    return this.get(new HttpParams().set("idInvoiceItem", idInvoiceItem).set("newPreTaxPrice", newPreTaxPrice), "invoice-item/edit-amount-reinvoiced");
  }
}
