import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Invoice } from '../../quotation/model/Invoice';
import { IQuotation } from '../../quotation/model/IQuotation';
import { InvoiceSearch } from '../model/InvoiceSearch';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService extends AppRestService<Invoice>{

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  addOrUpdateInvoice(invoice: Invoice) {
    return this.addOrUpdate(new HttpParams(), "invoice", invoice, "Enregistré", "Erreur lors de l'enregistrement");
  }

  getInvoiceForCustomerOrder(customerOrder: IQuotation) {
    return this.get(new HttpParams().set("customerOrderId", customerOrder.id), "invoice/customer-order");
  }

  getInvoicesList(invoiceSearch: InvoiceSearch) {
    return this.postList(new HttpParams(), "invoice/search", invoiceSearch);
  }

  getInvoiceById(invoiceId: number) {
    return this.getById("invoice", invoiceId);
  }

}
