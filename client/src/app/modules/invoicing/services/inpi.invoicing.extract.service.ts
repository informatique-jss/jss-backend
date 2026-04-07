import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { InpiInvoicingExtract } from '../../invoicing/model/InpiInvoicingExtract';

@Injectable({
  providedIn: 'root'
})
export class InpiInvoicingExtractService extends AppRestService<InpiInvoicingExtract> {

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getInpiInvoicingExtracts() {
    return this.getList(new HttpParams(), "inpi-invocing-extracts");
  }

  addOrUpdateInpiInvoicingExtract(inpiInvoicingExtract: InpiInvoicingExtract) {
    return this.addOrUpdate(new HttpParams(), "inpi-invoicing-extract", inpiInvoicingExtract, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
