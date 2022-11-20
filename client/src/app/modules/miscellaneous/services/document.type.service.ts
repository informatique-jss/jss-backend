import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { DocumentType } from '../../miscellaneous/model/DocumentType';

@Injectable({
  providedIn: 'root'
})
export class DocumentTypeService extends AppRestService<DocumentType>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getDocumentTypes() {
    return this.getListCached(new HttpParams(), "document-types");
  }

  addOrUpdateDocumentType(documentType: DocumentType) {
    this.clearListCache(new HttpParams(), "document-types");
    return this.addOrUpdate(new HttpParams(), "document-type", documentType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
