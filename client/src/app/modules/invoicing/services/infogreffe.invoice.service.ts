import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { InfogreffeInvoice } from '../../invoicing/model/InfogreffeInvoice';
import { Provision } from '../../quotation/model/Provision';

@Injectable({
  providedIn: 'root'
})
export class InfogreffeInvoiceService extends AppRestService<InfogreffeInvoice>{

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getInfogreffeInvoices() {
    return this.getList(new HttpParams(), "infogreffe-invoices");
  }

  getInfogreffeInvoicesByCustomerReference(customerReference: string) {
    return this.getList(new HttpParams().set("customerReference", customerReference), "infogreffe-invoices/search");
  }

  importCsv(csv: string) {
    return this.postItem(new HttpParams(), "infogreffe-invoice/import", csv);
  }

  createInvoiceFromGreffeInvoice(greffeInvoice: InfogreffeInvoice, provision: Provision) {
    return this.get(new HttpParams().set("infogreffeInvoiceId", greffeInvoice.id).set("provisionId", provision.id), "infogreffe-invoice/create");
  }
}
