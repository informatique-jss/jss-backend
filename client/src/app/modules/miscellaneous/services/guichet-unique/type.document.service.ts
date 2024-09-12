import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TypeDocument } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeDocument';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class TypeDocumentService extends AppRestService<TypeDocument> {

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getTypeDocument() {
    return this.getListCached(new HttpParams(), 'type-document');
  }

  addOrUpdateTypeDocument(typeDocument: TypeDocument) {
    this.clearListCache(new HttpParams(), "type-document");
    return this.addOrUpdate(new HttpParams(), "type-document", typeDocument, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
