import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Provision } from '../../quotation/model/Provision';
import { AzureInvoice } from '../model/AzureInvoice';

@Injectable({
  providedIn: 'root'
})
export class AzureInvoiceService extends AppRestService<AzureInvoice>{

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getAzureInvoices(displayOnlyToCheck: boolean) {
    return this.getList(new HttpParams().set("displayOnlyToCheck", displayOnlyToCheck), "azure-invoices");
  }

  getAzureInvoiceByInvoiceId(invoiceId: string) {
    return this.getList(new HttpParams().set("invoiceId", invoiceId), "azure-invoices/search");
  }

  getAzureInvoice(idInvoice: number) {
    return this.get(new HttpParams().set("idInvoice", idInvoice + ""), "azure-invoice");
  }

  updateAzureInvoice(azureInvoice: AzureInvoice) {
    return this.postItem(new HttpParams(), "azure-invoice", azureInvoice);
  }

  createInvoiceFromAzureInvoice(azureInvoice: AzureInvoice, provision: Provision) {
    return this.get(new HttpParams().set("azureInvoiceId", azureInvoice.id).set("provisionId", provision.id), "azure-invoice/create");
  }
}
