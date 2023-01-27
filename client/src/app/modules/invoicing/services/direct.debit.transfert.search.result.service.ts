import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { DirectDebitTransfertSearch } from '../model/DirectDebitTransfertSearch';
import { DirectDebitTransfertSearchResult } from '../model/DirectDebitTransfertSearchResult';

@Injectable({
  providedIn: 'root'
})
export class DirectDebitTransfertSearchResultService extends AppRestService<DirectDebitTransfertSearchResult>{
  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getTransferts(directDebitTransfertSearch: DirectDebitTransfertSearch) {
    return this.postList(new HttpParams(), "direct/transfert/search", directDebitTransfertSearch);
  }

  exportTransferts(directDebitTransfertSearch: DirectDebitTransfertSearch) {
    return this.downloadPost("direct/transfert/export", directDebitTransfertSearch as any);
  }
}
