import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Invoice } from '../../quotation/model/Invoice';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService extends AppRestService<Invoice>{

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  saveInvoice(invoice: Invoice) {
    return this.postItem(new HttpParams(), "invoice", invoice, "Facture enregistrée", "Erreur lors de l'enregistrement de la facture");
  }

  saveCreditNote(invoice: Invoice, idInvoiceForCreditNote: string) {
    return this.postItem(new HttpParams().set("idOriginInvoiceForCreditNote", idInvoiceForCreditNote), "invoice/credit-note", invoice, "Facture enregistrée", "Erreur lors de l'enregistrement de la facture");
  }

  cancelInvoice(invoice: Invoice) {
    return this.get(new HttpParams().set("idInvoice", invoice.id), "invoice/cancel");
  }

  getInvoiceById(invoiceId: number) {
    return this.getById("invoice", invoiceId);
  }

}
