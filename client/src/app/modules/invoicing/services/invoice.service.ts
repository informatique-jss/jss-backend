import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Invoice } from '../../quotation/model/Invoice';
import { IQuotation } from '../../quotation/model/IQuotation';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService extends AppRestService<Invoice>{

  saveInvoice(invoice: Invoice) {
    return this.postItem(new HttpParams(), "invoice", invoice, "Facture enregistrée", "Erreur lors de l'enregistrement de la facture");
  }

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getInvoiceForCustomerOrder(customerOrder: IQuotation) {
    return this.get(new HttpParams().set("customerOrderId", customerOrder.id), "invoice/customer-order");
  }

  getInvoiceById(invoiceId: number) {
    return this.getById("invoice", invoiceId);
  }

}
