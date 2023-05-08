import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AzureReceiptInvoice } from '../model/AzureReceiptInvoice';

@Injectable({
  providedIn: 'root'
})
export class AzureReceiptInvoiceService extends AppRestService<AzureReceiptInvoice>{

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  markAsReconciliated(azureReceiptInvoice: AzureReceiptInvoice, isReconciliated: boolean) {
    return this.get(new HttpParams().set("idAzureReceiptInvoice", azureReceiptInvoice.id).set("isReconciliated", isReconciliated), "azure-receipt/invoice/reconciliated");
  }

}
