import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AzureReceiptInvoice } from '../model/AzureReceiptInvoice';
import { AzureReceiptInvoiceStatus } from '../model/AzureReceiptInvoiceStatus';

@Injectable({
  providedIn: 'root'
})
export class AzureReceiptInvoiceStatusService extends AppRestService<AzureReceiptInvoiceStatus>{

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getInvoiceStatus(azureReceiptInvoice: AzureReceiptInvoice) {
    return this.get(new HttpParams().set("idAzureReceiptInvoice", azureReceiptInvoice.id), "azure-receipt/invoice/status");
  }

}
