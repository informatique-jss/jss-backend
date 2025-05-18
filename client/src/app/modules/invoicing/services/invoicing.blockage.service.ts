import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { InvoicingBlockage } from '../model/InvoicingBlockage';

@Injectable({
  providedIn: 'root'
})
export class InvoicingBlockageService extends AppRestService<InvoicingBlockage> {

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getInvoicingBlockages() {
    return this.getList(new HttpParams(), "invoicing-blockages");
  }

  addOrUpdateInvoicingBlockage(invoicingBlockage: InvoicingBlockage) {
    return this.addOrUpdate(new HttpParams(), "invoicing-blockage", invoicingBlockage, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
