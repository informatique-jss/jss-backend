import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { Document } from '../model/Document';

@Injectable({
  providedIn: 'root'
})
export class DocumentService extends AppRestService<Document> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getDocumentForCustomerOrder(idCustomerOrder: number) {
    return this.getList(new HttpParams().set("idCustomerOrder", idCustomerOrder), "order/documents");
  }

  getDocumentForQuotation(idQuotation: number) {
    return this.getList(new HttpParams().set("idQuotation", idQuotation), "quotation/documents");
  }

  getDocumentForResponsable(idResponsable: number) {
    return this.getList(new HttpParams().set("idResponsable", idResponsable), "responsable/documents");
  }

  addOrUpdateDocumentsForCustomerOrder(documents: Document[]) {
    return this.postItem(new HttpParams(), "order/documents", documents);
  }

  addOrUpdateDocumentsForQuotation(documents: Document[]) {
    return this.postItem(new HttpParams(), "quotation/documents", documents);
  }

  addOrUpdateDocumentsForResponsable(documents: Document[]) {
    return this.postItem(new HttpParams(), "responsable/documents", documents);
  }
}
