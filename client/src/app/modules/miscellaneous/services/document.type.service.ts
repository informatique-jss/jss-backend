import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { DocumentType } from "../../miscellaneous/model/DocumentType";

@Injectable({
  providedIn: 'root'
})
export class DocumentTypeService extends AppRestService<DocumentType>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getDocumentTypes() {
    return this.getList(new HttpParams(), "document-types");
  }

}
