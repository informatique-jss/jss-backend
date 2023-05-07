import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
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

  getAzureInvoice(idInvoice: number) {
    return this.get(new HttpParams().set("idInvoice", idInvoice + ""), "azure-invoice");
  }

  updateAzureInvoice(azureInvoice: AzureInvoice) {
    return this.postItem(new HttpParams(), "azure-invoice", azureInvoice);
  }
}
