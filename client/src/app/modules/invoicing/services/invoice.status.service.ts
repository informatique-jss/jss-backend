import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { InvoiceStatus } from '../../invoicing/model/InvoiceStatus';

@Injectable({
  providedIn: 'root'
})
export class InvoiceStatusService extends AppRestService<InvoiceStatus>{

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getInvoiceStatus() {
    return this.getList(new HttpParams(), "invoice-status-list");
  }

  addOrUpdateInvoiceStatus(invoiceStatus: InvoiceStatus) {
    return this.addOrUpdate(new HttpParams(), "invoice-status", invoiceStatus, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
