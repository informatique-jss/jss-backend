import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AccountingBalance } from '../model/AccountingBalance';
import { AccountingBalanceSearch } from '../model/AccountingBalanceSearch';

@Injectable({
  providedIn: 'root'
})
export class AccountingBalanceService extends AppRestService<AccountingBalance> {

  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  searchAccountingBalance(accountingBalanceSearch: AccountingBalanceSearch) {
    return this.postList(new HttpParams(), "accounting-balance/search", accountingBalanceSearch);
  }

  exportBalance(accountingBalanceSearch: AccountingBalanceSearch) {
    this.downloadPost(new HttpParams(), "accounting-balance/export", accountingBalanceSearch as any as AccountingBalance);
  }

  searchAccountingBalanceGenerale(accountingBalanceSearch: AccountingBalanceSearch) {
    return this.postList(new HttpParams(), "accounting-balance/generale/search", accountingBalanceSearch);
  }

  exportBalanceGenerale(accountingBalanceSearch: AccountingBalanceSearch) {
    this.downloadPost(new HttpParams(), "accounting-balance/generale/export", accountingBalanceSearch as any as AccountingBalance);
  }

}
