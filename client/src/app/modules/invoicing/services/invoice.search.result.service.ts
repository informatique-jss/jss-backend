import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { IQuotation } from '../../quotation/model/IQuotation';
import { InvoiceSearch } from '../model/InvoiceSearch';
import { InvoiceSearchResult } from '../model/InvoiceSearchResult';

@Injectable({
  providedIn: 'root'
})
export class InvoiceSearchResultService extends AppRestService<InvoiceSearchResult>{

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getInvoicesList(invoiceSearch: InvoiceSearch) {
    return this.postList(new HttpParams(), "invoice/search", invoiceSearch);
  }

  getInvoiceForCustomerOrder(customerOrder: IQuotation) {
    return this.getList(new HttpParams().set("customerOrderId", customerOrder.id), "invoice/customer-order");
  }

  getProviderInvoiceForCustomerOrder(customerOrder: IQuotation) {
    return this.getList(new HttpParams().set("customerOrderId", customerOrder.id), "invoice/customer-order/provider");
  }

}


