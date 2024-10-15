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

  addOrUpdateDocumentsForCustomerOrder(documents: Document[]) {
    return this.postItem(new HttpParams(), "order/documents", documents);
  }
}
