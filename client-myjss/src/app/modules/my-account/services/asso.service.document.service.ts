import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { AssoServiceDocument } from '../model/AssoServiceDocument';

@Injectable({
  providedIn: 'root'
})
export class AssoServiceDocumentService extends AppRestService<AssoServiceDocument> {


  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAssoServiceDocument(assoServiceDocument: AssoServiceDocument): Observable<AssoServiceDocument> {
    return this.get(new HttpParams().set('idAssoServiceDocument', assoServiceDocument.id), "attachment/asso-service-document");
  }
}
