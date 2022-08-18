import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Invoice } from '../../quotation/model/Invoice';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService extends AppRestService<Invoice>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  addOrUpdateInvoice(invoice: Invoice) {
    return this.addOrUpdate(new HttpParams(), "invoice", invoice, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
