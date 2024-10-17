import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { BankTransfertSearch } from '../model/BankTransfertSearch';
import { BankTransfertSearchResult } from '../model/BankTransfertSearchResult';

@Injectable({
  providedIn: 'root'
})
export class BankTransfertSearchResultService extends AppRestService<BankTransfertSearchResult> {
  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getTransferts(bankTransfertSearch: BankTransfertSearch) {
    return this.postList(new HttpParams(), "transfert/search", bankTransfertSearch);
  }

  exportTransferts(bankTransfertSearch: BankTransfertSearch, isOverrideExecutionDate: boolean) {
    return this.downloadPost(new HttpParams().set("isOverrideExecutionDate", isOverrideExecutionDate), "transfert/export", bankTransfertSearch as any, "Export");
  }
}
