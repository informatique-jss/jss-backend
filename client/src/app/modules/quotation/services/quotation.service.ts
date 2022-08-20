import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { IQuotation } from '../model/IQuotation';

@Injectable({
  providedIn: 'root'
})
export class QuotationService extends AppRestService<IQuotation>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  updateQuotationStatus(quotation: IQuotation) {
    return this.addOrUpdate(new HttpParams(), "quotation/status", quotation, "Devis enregistré", "Erreur lors de l'enregistrement du devis");
  }

  getQuotation(idQuotation: number) {
    return this.getById("quotation", idQuotation);
  }

  addOrUpdateQuotation(quotation: IQuotation) {
    return this.addOrUpdate(new HttpParams(), "quotation", quotation, "Devis enregistré", "Erreur lors de l'enregistrement du devis");
  }

  getInvoiceItemsForQuotation(quotation: IQuotation) {
    return this.addOrUpdate(new HttpParams(), "invoice-item/generate", quotation);
  }
}
