import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AccountingRecordSearch } from '../model/AccountingRecordSearch';
import { AccountingRecordSearchResult } from '../model/AccountingRecordSearchResult';

@Injectable({
  providedIn: 'root'
})
export class AccountingRecordSearchResultService extends AppRestService<AccountingRecordSearchResult> {

  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  searchAccountingRecords(accountingRecordSearch: AccountingRecordSearch) {
    return this.postList(new HttpParams(), "accounting-record/search", accountingRecordSearch);
  }

}
