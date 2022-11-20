import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DocumentExtension } from 'src/app/modules/quotation/model/guichet-unique/referentials/DocumentExtension';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class DocumentExtensionService extends AppRestService<DocumentExtension>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getDocumentExtension() {
    return this.getListCached(new HttpParams(), 'document-extension');
  }

}
